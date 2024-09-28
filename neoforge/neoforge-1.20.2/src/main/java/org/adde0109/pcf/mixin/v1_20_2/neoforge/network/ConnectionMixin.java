package org.adde0109.pcf.mixin.v1_20_2.neoforge.network;

import dev.neuralnexus.conditionalmixins.annotations.ReqMCVersion;
import dev.neuralnexus.conditionalmixins.annotations.ReqMappings;
import dev.neuralnexus.taterapi.Mappings;
import dev.neuralnexus.taterapi.MinecraftVersion;

import org.adde0109.pcf.common.abstractions.Connection;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.net.SocketAddress;

@ReqMappings(Mappings.MOJMAP)
@ReqMCVersion(min = MinecraftVersion.V1_20_2)
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
