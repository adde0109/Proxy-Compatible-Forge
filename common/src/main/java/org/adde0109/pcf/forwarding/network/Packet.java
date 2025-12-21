package org.adde0109.pcf.forwarding.network;

import io.netty.buffer.ByteBuf;

import org.jetbrains.annotations.NotNull;

public interface Packet {
    void write(final @NotNull ByteBuf buf);

    // void handle(final @NotNull T handler);
}
