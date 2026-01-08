package org.adde0109.pcf.v21_10.forwarding.network;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import org.jspecify.annotations.NonNull;

@AConstraint(mappings = Mappings.MOJANG)
public record MCQueryPayload(@NonNull ResourceLocation id, @NonNull ByteBuf data)
        implements net.minecraft.network.protocol.login.custom.CustomQueryPayload {
    @Override
    public void write(final @NonNull FriendlyByteBuf buf) {
        buf.writeBytes(this.data.slice());
    }
}
