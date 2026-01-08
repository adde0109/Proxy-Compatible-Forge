package org.adde0109.pcf.crossstitch;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

public interface SerializerBridge {
    void bridge$serializeToNetwork(final @NonNull Object argument, final @NonNull ByteBuf buffer);
}
