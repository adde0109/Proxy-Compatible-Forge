package org.adde0109.pcf.v20_2.neoforge.forwarding.network;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

@AConstraint(mappings = Mappings.MOJANG)
public record MCQueryPayload(@NotNull ResourceLocation id, @NotNull ByteBuf data)
        implements net.minecraft.network.protocol.login.custom.CustomQueryPayload {
    @Override
    public void write(final @NotNull FriendlyByteBuf buf) {
        buf.writeBytes(this.data.slice());
    }
}
