package org.adde0109.pcf.forwarding.network.codec;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface StreamEncoder<O, T> {
    void encode(final @NotNull O buffer, final @NotNull T value);
}
