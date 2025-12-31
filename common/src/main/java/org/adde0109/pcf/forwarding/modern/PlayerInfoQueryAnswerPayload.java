package org.adde0109.pcf.forwarding.modern;

import static org.adde0109.pcf.common.FriendlyByteBuf.readPayload;
import static org.adde0109.pcf.common.FriendlyByteBuf.writePayload;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.forwarding.network.CustomQueryAnswerPayload;
import org.adde0109.pcf.forwarding.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

/**
 * Payload response for the player info query
 *
 * @param data the buffer
 */
public record PlayerInfoQueryAnswerPayload(@NotNull ByteBuf data)
        implements CustomQueryAnswerPayload {
    public static final StreamCodec<@NotNull ByteBuf, @NotNull PlayerInfoQueryAnswerPayload>
            STREAM_CODEC =
                    CustomQueryAnswerPayload.codec(
                            PlayerInfoQueryAnswerPayload::write,
                            PlayerInfoQueryAnswerPayload::read);

    private static @NotNull PlayerInfoQueryAnswerPayload read(final @NotNull ByteBuf buf) {
        return new PlayerInfoQueryAnswerPayload(readPayload(buf));
    }

    private void write(final @NotNull ByteBuf buf) {
        writePayload(buf, this.data);
    }

    @Override
    public @NotNull StreamCodec<@NotNull ByteBuf, @NotNull PlayerInfoQueryAnswerPayload> codec() {
        return STREAM_CODEC;
    }
}
