package org.adde0109.pcf.mixin.v12_2.forge.forwarding.modern;

import static org.adde0109.pcf.forwarding.modern.ModernForwarding.handleHello;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.server.network.NetHandlerLoginServer;

import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V7, max = MinecraftVersion.V12_2))
@Mixin(NetHandlerLoginServer.class)
public abstract class ServerLoginPacketListenerImplHelloMixin
        implements ServerLoginPacketListenerBridge {
    // spotless:off
    @Inject(method = "processLoginStart*", // * b/c signature differs below 1.8.9
            cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 1,
            target = "Lnet/minecraft/server/network/NetHandlerLoginServer;currentLoginState:Lnet/minecraft/server/network/NetHandlerLoginServer$LoginState;"))
    // spotless:on
    private void onHandleHello(final @NotNull CallbackInfo ci) {
        handleHello(this, ci);
    }
}
