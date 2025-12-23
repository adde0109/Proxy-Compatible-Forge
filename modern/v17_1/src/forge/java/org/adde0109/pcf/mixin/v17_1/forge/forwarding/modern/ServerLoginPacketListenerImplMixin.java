package org.adde0109.pcf.mixin.v17_1.forge.forwarding.modern;

import static org.adde0109.pcf.common.Component.literal;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.DIRECT_CONNECT_ERR;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.FAILED_TO_VERIFY;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.forward;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.PLAYER_INFO_PAYLOAD;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.NameAndId;
import org.adde0109.pcf.common.reflection.StateUtil;
import org.adde0109.pcf.forwarding.modern.ModernForwarding;
import org.adde0109.pcf.forwarding.network.ClientboundCustomQueryPacket;
import org.adde0109.pcf.forwarding.network.ServerboundCustomQueryAnswerPacket;
import org.adde0109.pcf.mixin.v17_1.forge.forwarding.network.ConnectionAccessor;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        mappings = Mappings.SEARGE,
        version = @Versions(min = MinecraftVersion.V17, max = MinecraftVersion.V18_2))
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin {
    // spotless:off
    @Shadow @Final public net.minecraft.network.Connection connection;
    @Shadow @Nullable GameProfile gameProfile;
    @Shadow public abstract void shadow$disconnect(Component reason);

    @Unique private static final Logger pcf$LOGGER = LoggerFactory.getLogger("ServerLoginPacketListenerImpl");
    @Unique private int pcf$velocityLoginMessageId = -1;

    @Inject(method = "handleHello", cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 1,
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;state:Lnet/minecraft/server/network/ServerLoginPacketListenerImpl$State;"))
    // spotless:on
    private void onHandleHello(CallbackInfo ci) {
        if (PCF.instance().forwarding().enabled()) {
            this.pcf$velocityLoginMessageId = ThreadLocalRandom.current().nextInt();
            this.connection.send(
                    new ClientboundCustomQueryPacket(
                                    this.pcf$velocityLoginMessageId, PLAYER_INFO_PAYLOAD)
                            .toMC());
            PCF.logger.debug("Sent Forward Request");
            ci.cancel();
        }
    }

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleCustomQueryPacket(ServerboundCustomQueryPacket mcPacket, CallbackInfo ci) {
        if (PCF.instance().forwarding().enabled()
                && mcPacket.getTransactionId() == this.pcf$velocityLoginMessageId) {
            final ServerboundCustomQueryAnswerPacket packet =
                    ServerboundCustomQueryAnswerPacket.fromMC(mcPacket);

            if (packet.payload() == null) {
                this.shadow$disconnect(DIRECT_CONNECT_ERR());
                return;
            }
            final ByteBuf buf = packet.payload().data();

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
                pcf$LOGGER.info("UUID of player {} is {}", nameAndId.name(), nameAndId.id());
                StateUtil.setState(this, 3);
            } catch (Exception e) {
                this.shadow$disconnect(FAILED_TO_VERIFY());
                pcf$LOGGER.error("Exception while forwarding user {}", nameAndId.name());
                e.printStackTrace();
            }
            ci.cancel();
        }
    }

    @Shadow(remap = false)
    @SuppressWarnings({"MixinAnnotationTarget", "RedundantThrows"})
    void arclight$preLogin() throws Exception {}
}
