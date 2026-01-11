package org.adde0109.pcf.forwarding.modern;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readAddress;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readPayload;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readUUID;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readUtf;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readVarInt;

import static org.adde0109.pcf.forwarding.modern.VelocityProxy.Version.MODERN_FORWARDING_WITH_KEY;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.Version.MODERN_FORWARDING_WITH_KEY_V2;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.createProfile;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.readForwardedKey;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.readSignerUuidOrElse;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryAnswerPayload;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Payload response for the player info query.
 *
 * @param version the forwarding version used
 * @param address the forwarded client IP
 * @param profile the forwarded game profile
 * @param key the forwarded profile public key data
 * @param signer the signer's UUID
 */
public record PlayerInfoQueryAnswerPayload(
        VelocityProxy.Version version,
        @NonNull InetAddress address,
        @NonNull GameProfile profile,
        @Nullable ProfilePublicKeyData key,
        @Nullable UUID signer)
        implements CustomQueryAnswerPayload {
    public static final StreamCodec<@NonNull ByteBuf, @NonNull PlayerInfoQueryAnswerPayload>
            STREAM_CODEC =
                    CustomQueryAnswerPayload.codec(
                            PlayerInfoQueryAnswerPayload::write,
                            PlayerInfoQueryAnswerPayload::read);

    private static @NonNull PlayerInfoQueryAnswerPayload read(final @NonNull ByteBuf buf) {
        final ByteBuf data = readPayload(buf);
        final VelocityProxy.Version version = VelocityProxy.Version.from(readVarInt(data));
        final InetAddress address = readAddress(data);
        final UUID playerId = readUUID(data);
        final String playerName = readUtf(data, 16);
        final GameProfile profile = createProfile(playerId, playerName, data);
        ProfilePublicKeyData key = null;
        UUID signer = null;
        switch (version) {
            case MODERN_FORWARDING_WITH_KEY -> key = readForwardedKey(data);
            case MODERN_FORWARDING_WITH_KEY_V2 -> {
                key = readForwardedKey(data);
                signer = readSignerUuidOrElse(data, playerId);
            }
        }
        return new PlayerInfoQueryAnswerPayload(version, address, profile, key, signer);
    }

    private void write(final @NonNull ByteBuf buf) {
        throw new UnsupportedOperationException(
                this.getClass().getName() + " does not support serialization.");
    }

    @Override
    public @NonNull ByteBuf data() {
        throw new UnsupportedOperationException(
                this.getClass().getName() + " does not retain raw data.");
    }
}
