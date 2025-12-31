package org.adde0109.pcf.forwarding.modern;

import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_FORWARDING_WITH_KEY;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_FORWARDING_WITH_KEY_V2;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.createProfile;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.readForwardedKey;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.readSignerUuidOrElse;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readPayload;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readUUID;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readUtf;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readVarInt;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.forwarding.network.protocol.login.custom.CustomQueryAnswerPayload;
import org.adde0109.pcf.forwarding.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        int version,
        @NotNull String address,
        @NotNull GameProfile profile,
        @Nullable ProfilePublicKeyData key,
        @Nullable UUID signer)
        implements CustomQueryAnswerPayload {
    public static final StreamCodec<@NotNull ByteBuf, @NotNull PlayerInfoQueryAnswerPayload>
            STREAM_CODEC =
                    CustomQueryAnswerPayload.codec(
                            PlayerInfoQueryAnswerPayload::write,
                            PlayerInfoQueryAnswerPayload::read);

    private static @NotNull PlayerInfoQueryAnswerPayload read(final @NotNull ByteBuf buf) {
        final ByteBuf data = readPayload(buf);
        final int version = readVarInt(data);
        final String address = readUtf(data);
        final UUID playerId = readUUID(data);
        final String playerName = readUtf(data, 16);
        final GameProfile profile = createProfile(playerId, playerName, data);
        ProfilePublicKeyData key = null;
        if (version == MODERN_FORWARDING_WITH_KEY || version == MODERN_FORWARDING_WITH_KEY_V2) {
            key = readForwardedKey(data);
        }
        UUID signer = null;
        if (version == MODERN_FORWARDING_WITH_KEY_V2) {
            signer = readSignerUuidOrElse(data, playerId);
        }
        return new PlayerInfoQueryAnswerPayload(version, address, profile, key, signer);
    }

    private void write(final @NotNull ByteBuf buf) {
        throw new UnsupportedOperationException(
                this.getClass().getName() + " does not support serialization.");
    }

    @Override
    public @NotNull ByteBuf data() {
        throw new UnsupportedOperationException(
                this.getClass().getName() + " does not retain raw data.");
    }

    @Override
    public @NotNull StreamCodec<@NotNull ByteBuf, @NotNull PlayerInfoQueryAnswerPayload> codec() {
        return STREAM_CODEC;
    }
}
