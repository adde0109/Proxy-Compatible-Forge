package org.adde0109.pcf.forwarding.network.codec.adapter;

import java.util.HashSet;
import java.util.Set;

public final class AdapterRegistry {
    private final Set<AdapterCodec<?, ?>> CODECS = new HashSet<>();

    public void register(final AdapterCodec<?, ?>... codecs) {
        for (final AdapterCodec<?, ?> codec : codecs) {
            for (final AdapterCodec<?, ?> existingCodec : CODECS) {
                if (existingCodec.mcClass().equals(codec.mcClass())) {
                    throw new IllegalArgumentException(
                            "Duplicate codec MC class registration: " + codec.mcClass());
                } else if (existingCodec.objClass().equals(codec.objClass())) {
                    throw new IllegalArgumentException(
                            "Duplicate codec object class registration: " + codec.objClass());
                }
            }
            CODECS.add(codec);
        }
    }

    public AdapterCodec<?, ?> fromMC(final Class<?> mcClass) {
        for (final AdapterCodec<?, ?> codec : CODECS) {
            if (codec.mcClass().equals(mcClass)) {
                return codec;
            }
        }
        throw new IllegalArgumentException("No codec found for MC class: " + mcClass);
    }

    public AdapterCodec<?, ?> toMC(final Class<?> objClass) {
        for (final AdapterCodec<?, ?> codec : CODECS) {
            if (codec.objClass().equals(objClass)) {
                return codec;
            }
        }
        throw new IllegalArgumentException("No codec found for object class: " + objClass);
    }
}
