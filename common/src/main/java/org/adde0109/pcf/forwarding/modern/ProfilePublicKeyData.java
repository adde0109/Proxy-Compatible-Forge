package org.adde0109.pcf.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.serialization.Codec;

import org.adde0109.pcf.PCF;
import org.jspecify.annotations.NonNull;

import java.security.PublicKey;
import java.time.Instant;

/**
 * Wrapper for MC's ProfilePublicKey.Data
 *
 * @param expiresAt the expiration time
 * @param key the public key
 * @param keySignature the key signature
 */
public record ProfilePublicKeyData(
        @NonNull Instant expiresAt, @NonNull PublicKey key, byte[] keySignature) {
    public static final Codec<?, ProfilePublicKeyData> ADAPTER_CODEC;

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

    @SuppressWarnings("unchecked")
    public static <T> @NonNull ProfilePublicKeyData fromMC(final @NonNull T obj) {
        return ((Codec<T, ProfilePublicKeyData>) ADAPTER_CODEC).encode(obj).unwrap();
    }

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    public <T> @NonNull T toMC() {
        return ((Codec<T, ProfilePublicKeyData>) ADAPTER_CODEC).decode(this).unwrap();
    }
}
