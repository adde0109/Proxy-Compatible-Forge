package org.adde0109.pcf.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.forwarding.network.codec.adapter.AdapterCodec;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.time.Instant;

/**
 * Wrapper for MC's ProfilePublicKey.Data
 *
 * @param expiresAt the expiration time
 * @param key the public key
 * @param keySignature the key signature
 */
@SuppressWarnings("unchecked")
public record ProfilePublicKeyData(
        @NotNull Instant expiresAt, @NotNull PublicKey key, byte[] keySignature) {
    public static final AdapterCodec<?, ProfilePublicKeyData> ADAPTER_CODEC;

    static {
        if (Constraint.builder()
                .min(MinecraftVersions.V19)
                .max(MinecraftVersions.V19_2)
                .build()
                .result()) {
            ADAPTER_CODEC =
                    (AdapterCodec<?, ProfilePublicKeyData>)
                            PCF.instance().adapters().toMC(ProfilePublicKeyData.class);
        } else {
            PCF.logger.debug(
                    "Not loading ProfilePublicKeyData adapter, version not between 1.19 and 1.19.2");
            ADAPTER_CODEC = null;
        }
    }

    public static <T> @NotNull ProfilePublicKeyData fromMC(final @NotNull T obj) {
        assert ADAPTER_CODEC != null;
        return ((AdapterCodec<T, ProfilePublicKeyData>) ADAPTER_CODEC).fromMC(obj);
    }

    public <T> @NotNull T toMC() {
        assert ADAPTER_CODEC != null;
        return ((AdapterCodec<T, ProfilePublicKeyData>) ADAPTER_CODEC).toMC(this);
    }
}
