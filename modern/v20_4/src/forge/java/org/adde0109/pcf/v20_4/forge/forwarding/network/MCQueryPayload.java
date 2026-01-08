package org.adde0109.pcf.v20_4.forge.forwarding.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.custom.CustomQueryPayload;
import net.minecraft.resources.ResourceLocation;

public record MCQueryPayload(ResourceLocation id, ByteBuf data) implements CustomQueryPayload {
    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeBytes(this.data.slice());
    }
}
