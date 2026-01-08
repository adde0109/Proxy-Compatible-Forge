package org.adde0109.pcf.mixin.v21_11.forwarding.modern;

import static org.adde0109.pcf.forwarding.modern.ModernForwarding.handleHello;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.jspecify.annotations.NonNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// spotless:off
@AConstraint(version = @Versions(min = MinecraftVersion.V17))
@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
@Mixin(targets = "net.minecraft.server.network.ServerLoginPacketListenerImpl")
public abstract class ServerLoginPacketListenerImplHelloMixin
        implements ServerLoginPacketListenerBridge {
    @AConstraint(mappings = Mappings.SEARGE, version = @Versions(min = MinecraftVersion.V17, max = MinecraftVersion.V18_2))
    @Inject(method = "m_5990_", cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 1,
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;f_10019_:Lnet/minecraft/server/network/ServerLoginPacketListenerImpl$State;"))
    private void onHandleHello_17(final @NonNull CallbackInfo ci) {
        handleHello(this, ci);
    }

    @AConstraint(mappings = Mappings.SEARGE, version = @Versions(min = MinecraftVersion.V19, max = MinecraftVersion.V20_1))
    @Inject(method = "m_5990_", cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 2,
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;f_10019_:Lnet/minecraft/server/network/ServerLoginPacketListenerImpl$State;"))
    private void onHandleHello_19(final @NonNull CallbackInfo ci) {
        handleHello(this, ci);
    }

    @AConstraint(mappings = Mappings.SEARGE, version = @Versions(min = MinecraftVersion.V20_2, max = MinecraftVersion.V20_4))
    @Inject(method = "m_5990_", cancellable = true, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;m_294008_(Lcom/mojang/authlib/GameProfile;)V"))
    private void onHandleHello_20_S(final @NonNull CallbackInfo ci) {
        handleHello(this, ci);
    }

    @AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V20_2))
    @Inject(method = "handleHello", cancellable = true, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;startClientVerification(Lcom/mojang/authlib/GameProfile;)V"))
    private void onHandleHello_20_M(final @NonNull CallbackInfo ci) {
        handleHello(this, ci);
    }
}
// spotless:on
