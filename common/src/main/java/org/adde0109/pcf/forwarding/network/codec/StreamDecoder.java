package org.adde0109.pcf.forwarding.network.codec;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface StreamDecoder<I, T> {
    T decode(final @NotNull I buffer);
}
