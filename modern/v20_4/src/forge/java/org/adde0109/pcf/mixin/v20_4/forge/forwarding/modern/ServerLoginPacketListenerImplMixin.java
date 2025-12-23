package org.adde0109.pcf.mixin.v20_4.forge.forwarding.modern;

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

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.NameAndId;
import org.adde0109.pcf.forwarding.modern.ModernForwarding;
import org.adde0109.pcf.forwarding.network.ClientboundCustomQueryPacket;
import org.adde0109.pcf.forwarding.network.ServerboundCustomQueryAnswerPacket;
import org.adde0109.pcf.mixin.v17_1.forge.forwarding.network.ConnectionAccessor;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Adapted from <a
 * href="https://github.com/PaperMC/Paper/blob/main/paper-server/patches/sources/net/minecraft/server/network/ServerLoginPacketListenerImpl.java.patch">PaperMC</a>
 */
@AConstraint(
        mappings = Mappings.SEARGE,
        version = @Versions(min = MinecraftVersion.V20_2, max = MinecraftVersion.V20_4))
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin {
    // spotless:off
    @Shadow @Final static Logger LOGGER;
    @Shadow @Final Connection connection;
    @Shadow abstract void shadow$startClientVerification(GameProfile profile);
    @Shadow public abstract void shadow$disconnect(Component reason);

    @Unique private int pcf$velocityLoginMessageId = -1;

    @Inject(method = "handleHello", cancellable = true, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;startClientVerification(Lcom/mojang/authlib/GameProfile;)V"))
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
    private void onHandleCustomQueryPacket(
            net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket mcPacket,
            CallbackInfo ci) {
        if (PCF.instance().forwarding().enabled()
                && mcPacket.transactionId() == this.pcf$velocityLoginMessageId) {
            final ServerboundCustomQueryAnswerPacket packet =
                    ServerboundCustomQueryAnswerPacket.fromMC(mcPacket);

            if (packet.payload() == null) {
                this.shadow$disconnect(DIRECT_CONNECT_ERR());
                ci.cancel();
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
                LOGGER.info("UUID of player {} is {}", nameAndId.name(), nameAndId.id());
                this.shadow$startClientVerification(data.profile());
            } catch (Exception e) {
                this.shadow$disconnect(FAILED_TO_VERIFY());
                LOGGER.error("Exception while forwarding user {}", nameAndId.name());
                e.printStackTrace();
            }
            ci.cancel();
        }
    }

    @Shadow(remap = false)
    @SuppressWarnings({"MixinAnnotationTarget", "RedundantThrows"})
    void arclight$preLogin() throws Exception {}
}
