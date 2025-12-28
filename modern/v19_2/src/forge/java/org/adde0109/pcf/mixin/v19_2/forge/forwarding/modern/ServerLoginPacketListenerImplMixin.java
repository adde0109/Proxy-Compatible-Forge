package org.adde0109.pcf.mixin.v19_2.forge.forwarding.modern;

import static org.adde0109.pcf.forwarding.modern.ModernForwarding.handleCustomQueryPacket;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.handleHello;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.mixin.MixinCancellableCallbackWrapper;

import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adapted from: <br>
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/bef2c9d005bdd039f188ee53094a928e76bd8e59/patches/server/0273-Add-Velocity-IP-Forwarding-Support.patch">Paper
 * 1.19.2</a> <br>
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/4074d4ee99a75ad005b05bfba8257e55beeb335f/patches/server/0884-Add-Velocity-IP-Forwarding-Support.patch">Paper
 * 1.19.3</a> <br>
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/ver/1.19.4/patches/server/0874-Add-Velocity-IP-Forwarding-Support.patch">Paper
 * 1.19.4</a>
 */
@AConstraint(
        mappings = Mappings.SEARGE,
        version = @Versions(min = MinecraftVersion.V19, max = MinecraftVersion.V19_2))
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin
        implements ServerLoginPacketListenerBridge {
    // spotless:off
    // TODO: Test Muxin method annotations to combine 1.19.x mixins
    @Inject(method = "handleHello", cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 1,
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;state:Lnet/minecraft/server/network/ServerLoginPacketListenerImpl$State;"))
    // spotless:on
    private void onHandleHello(CallbackInfo ci) {
        handleHello(this, ci);
    }

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleCustomQueryPacket(ServerboundCustomQueryPacket packet, CallbackInfo ci) {
        handleCustomQueryPacket(
                this, packet.getTransactionId(), packet, new MixinCancellableCallbackWrapper(ci));
    }
}
