package org.adde0109.pcf.forwarding.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.jetbrains.annotations.NotNull;

public record CustomQueryPayloadImpl(@NotNull String id, @NotNull ByteBuf data)
        implements CustomQueryPayload {
    @Override
    public @NotNull ByteBuf data() {
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(this.data);
        return buf;
    }

    @Override
    public void write(final @NotNull ByteBuf buf) {
        buf.writeBytes(this.data);
    }
}
