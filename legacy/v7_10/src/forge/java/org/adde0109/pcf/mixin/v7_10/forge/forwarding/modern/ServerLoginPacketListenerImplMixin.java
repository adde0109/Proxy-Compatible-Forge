package org.adde0109.pcf.mixin.v7_10.forge.forwarding.modern;

import static org.adde0109.pcf.forwarding.modern.ModernForwarding.handleCustomQueryPacket;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.handleHello;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.server.network.NetHandlerLoginServer;

import org.adde0109.pcf.forwarding.modern.DummyCancellable;
import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.adde0109.pcf.v7_10.forge.forwarding.network.C2SCustomQueryAnswerPacket;
import org.adde0109.pcf.v7_10.forge.forwarding.network.ServerLoginQueryListener;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/ver/1.19.4/patches/server/0874-Add-Velocity-IP-Forwarding-Support.patch">Adapted
 * from Paper</a>
 */
@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V7, max = MinecraftVersion.V7_10))
@Implements(@Interface(iface = ServerLoginQueryListener.class, prefix = "pcf$"))
@Mixin(NetHandlerLoginServer.class)
public abstract class ServerLoginPacketListenerImplMixin
        implements ServerLoginPacketListenerBridge {
    // spotless:off
    @Inject(method = "processLoginStart", cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 1,
            target = "Lnet/minecraft/server/network/NetHandlerLoginServer;currentLoginState:Lnet/minecraft/server/network/NetHandlerLoginServer$LoginState;"))
    // spotless:on
    private void onHandleHello(CallbackInfo ci) {
        handleHello(this, ci);
    }

    public void pcf$handleCustomQueryPacket(C2SCustomQueryAnswerPacket packet) {
        handleCustomQueryPacket(this, packet.transactionId(), packet, DummyCancellable.INSTANCE);
    }
}
