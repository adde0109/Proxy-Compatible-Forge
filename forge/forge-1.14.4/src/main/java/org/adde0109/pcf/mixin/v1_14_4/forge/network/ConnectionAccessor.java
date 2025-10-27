package org.adde0109.pcf.mixin.v1_14_4.forge.network;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import net.minecraft.network.Connection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.SocketAddress;

@ReqMappings(Mappings.LEGACY_SEARGE)
@Mixin(Connection.class)
public interface ConnectionAccessor {
    @Accessor("address")
    SocketAddress pcf$getAddress();

    @Accessor("address")
    void pcf$setAddress(SocketAddress address);
}
