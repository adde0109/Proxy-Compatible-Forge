package org.adde0109.pcf.forwarding.network.codec.adapter;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface AdapterDecoder<I, T> {
    @NotNull T fromMC(final @NotNull I mcObject);
}
