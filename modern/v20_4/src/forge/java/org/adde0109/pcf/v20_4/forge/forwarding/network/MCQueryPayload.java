package org.adde0109.pcf.v20_4.forge.forwarding.network;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@AConstraint(mappings = Mappings.SEARGE)
public record MCQueryPayload(ResourceLocation id, ByteBuf data)
        implements net.minecraft.network.protocol.login.custom.CustomQueryPayload {
    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeBytes(this.data.slice());
    }
}
