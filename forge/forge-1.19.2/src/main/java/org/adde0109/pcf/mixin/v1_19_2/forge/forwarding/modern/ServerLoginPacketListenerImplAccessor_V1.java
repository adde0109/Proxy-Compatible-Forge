package org.adde0109.pcf.mixin.v1_19_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.entity.player.ProfilePublicKey;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@ReqMappings(Mappings.SEARGE)
@ReqMCVersion(MinecraftVersion.V19)
@Mixin(ServerLoginPacketListenerImpl.class)
public interface ServerLoginPacketListenerImplAccessor_V1 {
    @SuppressWarnings("MixinAnnotationTarget")
    @Accessor("f_215255_") // playerProfilePublicKey
    void pcf$setPlayerProfilePublicKey(@Nullable ProfilePublicKey publicKey);
}
