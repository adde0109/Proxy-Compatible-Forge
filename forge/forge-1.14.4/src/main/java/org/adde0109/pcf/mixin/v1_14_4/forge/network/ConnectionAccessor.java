package org.adde0109.pcf.mixin.v1_14_4.forge.network;

import net.minecraft.network.Connection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.SocketAddress;

@Mixin(Connection.class)
public interface ConnectionAccessor {
    @Accessor("address")
    SocketAddress pcf$getAddress();

    @Accessor("address")
    void pcf$setAddress(SocketAddress address);
}
