package org.adde0109.pcf.mixin.v20_2.neoforge.forwarding.modern;

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
    SocketAddress bridge$address();

    @Accessor("address")
    void bridge$address(SocketAddress address);
}
