package org.adde0109.pcf.v21_11.forwarding.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.custom.CustomQueryPayload;
import net.minecraft.resources.Identifier;

import org.jspecify.annotations.NonNull;

public record MCQueryPayload(@NonNull Identifier id, @NonNull ByteBuf data)
        implements CustomQueryPayload {
    @Override
    public void write(final @NonNull FriendlyByteBuf buf) {
        buf.writeBytes(this.data.slice());
    }
}
