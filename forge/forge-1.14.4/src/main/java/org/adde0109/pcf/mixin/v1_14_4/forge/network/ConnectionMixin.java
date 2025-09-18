package org.adde0109.pcf.mixin.v1_14_4.forge.network;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import org.adde0109.pcf.common.abstractions.Connection;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.net.SocketAddress;

@ReqMappings(Mappings.SEARGE)
@ReqMCVersion(min = MinecraftVersion.V14, max = MinecraftVersion.V16_5)
@Mixin(net.minecraft.network.Connection.class)
@Implements(@Interface(iface = Connection.class, prefix = "conn$", remap = Interface.Remap.NONE))
public abstract class ConnectionMixin {
    @Shadow private SocketAddress address;

    public SocketAddress conn$remoteAddress() {
        return this.address;
    }

    public void conn$setAddress(SocketAddress address) {
        this.address = address;
    }
}
