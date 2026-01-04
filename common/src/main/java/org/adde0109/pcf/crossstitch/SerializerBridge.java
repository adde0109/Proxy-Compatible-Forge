package org.adde0109.pcf.crossstitch;

import io.netty.buffer.ByteBuf;

import org.jetbrains.annotations.NotNull;

public interface SerializerBridge {
    void bridge$serializeToNetwork(final @NotNull Object argument, final @NotNull ByteBuf buffer);
}
