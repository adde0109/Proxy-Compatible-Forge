package org.adde0109.pcf.mixin.v20_2.neoforge.forwarding.modern;

import static org.adde0109.pcf.forwarding.modern.ModernForwarding.handleCustomQueryPacket;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.handleHello;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adapted from <a
 * href="https://github.com/PaperMC/Paper/blob/main/paper-server/patches/sources/net/minecraft/server/network/ServerLoginPacketListenerImpl.java.patch">PaperMC</a>
 */
@AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V20_2))
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin
        implements ServerLoginPacketListenerBridge {
    // spotless:off
    @Inject(method = "handleHello", cancellable = true, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;startClientVerification(Lcom/mojang/authlib/GameProfile;)V"))
    // spotless:on
    private void onHandleHello(final @NotNull CallbackInfo ci) {
        handleHello(this, ci);
    }

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleCustomQueryPacket(
            final @NotNull ServerboundCustomQueryAnswerPacket packet,
            final @NotNull CallbackInfo ci) {
        handleCustomQueryPacket(this, packet.transactionId(), packet, ci);
    }
}
