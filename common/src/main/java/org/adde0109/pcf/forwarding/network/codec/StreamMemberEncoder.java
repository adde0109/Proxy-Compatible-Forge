package org.adde0109.pcf.forwarding.network.codec;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface StreamMemberEncoder<O, T> {
    void encode(final @NotNull T value, final @NotNull O output);
}
