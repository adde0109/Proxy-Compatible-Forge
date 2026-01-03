package org.adde0109.pcf.forwarding.modern;

import dev.neuralnexus.taterapi.adapter.AdapterCodec;
import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import org.adde0109.pcf.PCF;
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
        if (Constraint.range(MinecraftVersions.V19, MinecraftVersions.V19_2).result()) {
            ADAPTER_CODEC =
                    PCF.instance().adapters().getTo(ProfilePublicKeyData.class).orElse(null);
        } else {
            PCF.logger.debug(
                    "Not loading ProfilePublicKeyData adapter, version not between 1.19 and 1.19.2");
            ADAPTER_CODEC = null;
        }
    }

    public static <T> @NotNull ProfilePublicKeyData fromMC(final @NotNull T obj) {
        assert ADAPTER_CODEC != null;
        return ((AdapterCodec<T, ProfilePublicKeyData>) ADAPTER_CODEC).from(obj);
    }

    public <T> @NotNull T toMC() {
        assert ADAPTER_CODEC != null;
        return ((AdapterCodec<T, ProfilePublicKeyData>) ADAPTER_CODEC).to(this);
    }
}
