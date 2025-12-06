package org.adde0109.pcf.mixin.v1_19_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.UUID;

@AConstraint(
        mappings = Mappings.SEARGE,
        version = @Versions(min = MinecraftVersion.V19_1, max = MinecraftVersion.V19_2))
@Mixin(ServerLoginPacketListenerImpl.class)
public interface ServerLoginPacketListenerImplAccessor_V2 {
    @Accessor("profilePublicKeyData")
    @Nullable ProfilePublicKey.Data pcf$profilePublicKeyData();

    @Accessor("profilePublicKeyData")
    void pcf$setProfilePublicKeyData(@Nullable ProfilePublicKey.Data keyData);

    @SuppressWarnings("UnusedReturnValue")
    @Invoker("validatePublicKey")
    static ProfilePublicKey pcf$validatePublicKey(
            ProfilePublicKey.Data keyData,
            UUID signer,
            SignatureValidator validator,
            boolean enforceSecureProfile) {
        throw new UnsupportedOperationException();
    }
}
