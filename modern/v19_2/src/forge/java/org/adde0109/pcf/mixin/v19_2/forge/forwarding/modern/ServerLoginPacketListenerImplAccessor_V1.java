package org.adde0109.pcf.mixin.v19_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.entity.player.ProfilePublicKey;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@AConstraint(mappings = Mappings.SEARGE, version = @Versions(MinecraftVersion.V19))
@Mixin(ServerLoginPacketListenerImpl.class)
public interface ServerLoginPacketListenerImplAccessor_V1 {
    @SuppressWarnings("MixinAnnotationTarget")
    @Accessor("f_215255_") // playerProfilePublicKey
    void pcf$setPlayerProfilePublicKey(@Nullable ProfilePublicKey publicKey);
}
