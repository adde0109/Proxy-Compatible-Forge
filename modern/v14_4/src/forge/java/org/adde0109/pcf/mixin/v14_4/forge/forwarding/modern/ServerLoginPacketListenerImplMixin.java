package org.adde0109.pcf.mixin.v14_4.forge.forwarding.modern;

import static org.adde0109.pcf.common.Component.literal;
import static org.adde0109.pcf.common.Component.translatable;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.DIRECT_CONNECT_ERR;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.forward;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.PLAYER_INFO_CHANNEL;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.PLAYER_INFO_PACKET;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.NameAndId;
import org.adde0109.pcf.forwarding.modern.ModernForwarding;
import org.adde0109.pcf.mixin.v14_4.forge.network.ConnectionAccessor;
import org.adde0109.pcf.v14_4.forge.reflection.StateUtil;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/ver/1.19.4/patches/server/0874-Add-Velocity-IP-Forwarding-Support.patch">Adapted
 * from Paper</a>
 */
@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V14, max = MinecraftVersion.V16_5))
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin {
    // spotless:off
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final public net.minecraft.network.Connection connection;
    @Shadow private GameProfile gameProfile;
    @Shadow public abstract void shadow$disconnect(Component reason);

    @Unique private int pcf$velocityLoginMessageId = -1;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "handleHello", cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 1,
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;state:Lnet/minecraft/server/network/ServerLoginPacketListenerImpl$State;"))
    private void onHandleHello(ServerboundHelloPacket packet, CallbackInfo ci) {
        Validate.validState(StateUtil.stateEquals(this, 0), "Unexpected hello packet");
        if (PCF.instance().forwarding().enabled()) {
            this.pcf$velocityLoginMessageId = ThreadLocalRandom.current().nextInt();
            ClientboundCustomQueryPacket queryPacket = new ClientboundCustomQueryPacket();
            ((ClientboundCustomQueryPacketAccessor) queryPacket).pcf$setTransactionId(this.pcf$velocityLoginMessageId);
            ((ClientboundCustomQueryPacketAccessor) queryPacket).pcf$setIdentifier(PLAYER_INFO_CHANNEL());
            ((ClientboundCustomQueryPacketAccessor) queryPacket).pcf$setData(new FriendlyByteBuf(PLAYER_INFO_PACKET));
            this.connection.send(queryPacket);
            PCF.logger.debug("Sent Forward Request");
            ci.cancel();
        }
    }
    // spotless:on

    @SuppressWarnings("DuplicatedCode")
    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleCustomQueryPacket(ServerboundCustomQueryPacket packet, CallbackInfo ci) {
        if (PCF.instance().forwarding().enabled()
                && ((ServerboundCustomQueryPacketAccessor) packet).pcf$getTransactionId()
                        == this.pcf$velocityLoginMessageId) {
            final ByteBuf buf = ((ServerboundCustomQueryPacketAccessor) packet).pcf$getData();
            if (buf == null) {
                this.shadow$disconnect(DIRECT_CONNECT_ERR());
                return;
            }

            final ModernForwarding.Data data =
                    forward(buf, ((ConnectionAccessor) this.connection).pcf$getAddress());
            if (data.disconnectMsg() != null) {
                this.shadow$disconnect(literal(data.disconnectMsg()));
                ci.cancel();
                return;
            }
            ((ConnectionAccessor) this.connection).pcf$setAddress(data.address());

            final NameAndId nameAndId = new NameAndId(data.profile());

            // Proceed with login
            try {
                // TODO: Pull this into a common compat class
                if (MetaAPI.instance().isPlatformPresent(Platforms.ARCLIGHT)) {
                    this.arclight$preLogin();
                    ci.cancel();
                    return;
                }
                this.gameProfile = data.profile();
                LOGGER.info("UUID of player {} is {}", nameAndId.name(), nameAndId.id());
                StateUtil.setState(this, 3);
            } catch (Exception ex) {
                this.shadow$disconnect(translatable("multiplayer.disconnect.unverified_username"));
                PCF.logger.warn("Exception verifying " + nameAndId.name(), ex);
            }
            ci.cancel();
        }
    }

    @Shadow(remap = false)
    @SuppressWarnings({"MixinAnnotationTarget", "RedundantThrows"})
    void arclight$preLogin() throws Exception {}
}
