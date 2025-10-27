package org.adde0109.pcf.mixin.v1_20_2.neoforge.network;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import org.adde0109.pcf.common.Connection;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@ReqMappings(Mappings.MOJANG)
@ReqMCVersion(min = MinecraftVersion.V20_2)
@Mixin(net.minecraft.network.Connection.class)
@Implements(@Interface(iface = Connection.class, prefix = "conn$", remap = Interface.Remap.NONE))
public abstract class ConnectionMixin {
    @Shadow private SocketAddress address;

    public InetSocketAddress conn$remoteAddress() {
        return (InetSocketAddress) this.address;
    }

    public void conn$setAddress(SocketAddress address) {
        this.address = address;
    }
}
