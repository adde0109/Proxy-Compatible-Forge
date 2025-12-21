package org.adde0109.pcf.forwarding.network;

import io.netty.buffer.ByteBuf;

import org.jetbrains.annotations.NotNull;

public interface CustomQueryAnswerPayload {
    @NotNull ByteBuf data();

    void write(final @NotNull ByteBuf buf);
}
