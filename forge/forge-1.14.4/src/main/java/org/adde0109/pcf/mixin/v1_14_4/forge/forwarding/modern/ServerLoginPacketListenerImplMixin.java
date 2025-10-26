package org.adde0109.pcf.mixin.v1_14_4.forge.forwarding.modern;

import static org.adde0109.pcf.forwarding.modern.ModernForwarding.forward;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MAX_SUPPORTED_FORWARDING_VERSION;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.QUERY_IDS;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.createProfile;
import static org.adde0109.pcf.v1_14_4.forge.forwarding.FWDBootstrap.COMPONENT;
import static org.adde0109.pcf.v1_14_4.forge.forwarding.FWDBootstrap.PLAYER_INFO_CHANNEL;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.Connection;
import org.adde0109.pcf.common.NameAndId;
import org.adde0109.pcf.v1_14_4.forge.reflection.StateUtil;
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

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/ver/1.19.4/patches/server/0874-Add-Velocity-IP-Forwarding-Support.patch">Adapted
 * from Paper</a>
 */
@ReqMappings(Mappings.LEGACY_SEARGE)
@ReqMCVersion(min = MinecraftVersion.V14, max = MinecraftVersion.V16_5)
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin {
    // spotless:off
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final public net.minecraft.network.Connection connection;
    @Shadow private GameProfile gameProfile;
    @Shadow public abstract void shadow$disconnect(Component reason);

    @Unique private int pcf$velocityLoginMessageId = -1;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "handleHello", cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;state:Lnet/minecraft/server/network/ServerLoginPacketListenerImpl$State;"))
    private void onHandleHello(ServerboundHelloPacket packet, CallbackInfo ci) {
        Validate.validState(StateUtil.stateEquals(this, 0), "Unexpected hello packet");
        if (PCF.instance().forwarding().enabled()) {
            this.pcf$velocityLoginMessageId = ThreadLocalRandom.current().nextInt();
            final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeByte(MAX_SUPPORTED_FORWARDING_VERSION);
            ClientboundCustomQueryPacket queryPacket = new ClientboundCustomQueryPacket();
            ((ClientboundCustomQueryPacketAccessor) queryPacket).setTransactionId(this.pcf$velocityLoginMessageId);
            ((ClientboundCustomQueryPacketAccessor) queryPacket).setIdentifier(PLAYER_INFO_CHANNEL);
            ((ClientboundCustomQueryPacketAccessor) queryPacket).setData(buf);
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
                && ((ServerboundCustomQueryPacketAccessor) packet).getTransactionId()
                        == this.pcf$velocityLoginMessageId) {
            QUERY_IDS.remove(this.pcf$velocityLoginMessageId);

            final ByteBuf buf = ((ServerboundCustomQueryPacketAccessor) packet).getData();
            if (buf == null) {
                this.shadow$disconnect(
                        COMPONENT.apply("This server requires you to connect with Velocity."));
                return;
            }

            final Optional<String> disconnect = forward(buf, (Connection) this.connection);
            if (disconnect.isPresent()) {
                this.shadow$disconnect(COMPONENT.apply(disconnect.get()));
                ci.cancel();
                return;
            }

            this.gameProfile = createProfile(buf);
            final NameAndId nameAndId = new NameAndId(this.gameProfile);

            // TODO Update handling for lazy sessions, might not even have to do anything?

            // Proceed with login
            try {
                // TODO: Pull this into a common compat class
                if (MetaAPI.instance().isPlatformPresent(Platforms.ARCLIGHT)) {
                    this.arclight$preLogin();
                    ci.cancel();
                    return;
                }
                LOGGER.info("UUID of player {} is {}", nameAndId.name(), nameAndId.id());
                org.adde0109.pcf.common.reflection.StateUtil.setState(this, 3);
            } catch (Exception ex) {
                this.shadow$disconnect(COMPONENT.apply("Failed to verify username!"));
                PCF.logger.warn("Exception verifying " + nameAndId.name(), ex);
            }
            ci.cancel();
        }
    }

    @Shadow(remap = false)
    @SuppressWarnings({"MixinAnnotationTarget", "RedundantThrows"})
    void arclight$preLogin() throws Exception {}
}
