package org.adde0109.pcf.forwarding.modern;

import static org.adde0109.pcf.common.FriendlyByteBuf.readPayload;
import static org.adde0109.pcf.common.FriendlyByteBuf.readUUID;
import static org.adde0109.pcf.common.FriendlyByteBuf.readUtf;
import static org.adde0109.pcf.common.FriendlyByteBuf.readVarInt;
import static org.adde0109.pcf.common.FriendlyByteBuf.writePayload;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_FORWARDING_WITH_KEY;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_FORWARDING_WITH_KEY_V2;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.createProfile;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.readForwardedKey;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.readSignerUuidOrElse;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.forwarding.network.CustomQueryAnswerPayload;
import org.adde0109.pcf.forwarding.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Payload response for the player info query
 *
 * @param data the buffer
 */
public record PlayerInfoQueryAnswerPayload(
        @NotNull ByteBuf data,
        int version,
        @NotNull String address,
        @NotNull GameProfile profile,
        @Nullable VelocityProxy.ProfilePublicKeyData key,
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
        VelocityProxy.ProfilePublicKeyData key = null;
        if (version == MODERN_FORWARDING_WITH_KEY || version == MODERN_FORWARDING_WITH_KEY_V2) {
            key = readForwardedKey(data);
        }
        UUID signer = null;
        if (version == MODERN_FORWARDING_WITH_KEY_V2) {
            signer = readSignerUuidOrElse(data, playerId);
        }
        return new PlayerInfoQueryAnswerPayload(data, version, address, profile, key, signer);
    }

    private void write(final @NotNull ByteBuf buf) {
        writePayload(buf, this.data);
    }

    @Override
    public @NotNull StreamCodec<@NotNull ByteBuf, @NotNull PlayerInfoQueryAnswerPayload> codec() {
        return STREAM_CODEC;
    }
}
