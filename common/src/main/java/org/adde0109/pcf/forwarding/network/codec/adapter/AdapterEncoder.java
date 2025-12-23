package org.adde0109.pcf.forwarding.network.codec.adapter;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface AdapterEncoder<O, T> {
    @NotNull O toMC(final @NotNull T object);
}
