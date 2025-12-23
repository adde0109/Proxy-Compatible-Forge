package org.adde0109.pcf.mixin.v20_2.neoforge.forwarding;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;

import net.minecraft.network.Connection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.SocketAddress;

@AConstraint(mappings = Mappings.MOJANG)
@Mixin(Connection.class)
public interface ConnectionAccessor {
    @Accessor("address")
    SocketAddress pcf$address();

    @Accessor("address")
    void pcf$address(SocketAddress address);
}
