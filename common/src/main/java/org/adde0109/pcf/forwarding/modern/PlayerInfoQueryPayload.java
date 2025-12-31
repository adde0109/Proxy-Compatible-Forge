package org.adde0109.pcf.forwarding.modern;

import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readPayload;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readResourceLocation;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.writePayload;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.writeResourceLocation;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.forwarding.network.codec.StreamCodec;
import org.adde0109.pcf.forwarding.network.protocol.login.custom.CustomQueryPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Payload for the player info query <br>
 * Adapted from <a
 * href="https://github.com/PaperMC/Paper/blob/main/paper-server/patches/sources/net/minecraft/network/protocol/login/ClientboundCustomQueryPacket.java.patch#L8-L15">PaperMC</a>
 *
 * @param data the buffer
 */
public record PlayerInfoQueryPayload(@NotNull ByteBuf data) implements CustomQueryPayload {
    public static final StreamCodec<ByteBuf, PlayerInfoQueryPayload> STREAM_CODEC =
            CustomQueryPayload.codec(PlayerInfoQueryPayload::write, PlayerInfoQueryPayload::read);

    private static @NotNull PlayerInfoQueryPayload read(final @NotNull ByteBuf buf) {
        readResourceLocation(buf); // Discard
        return new PlayerInfoQueryPayload(readPayload(buf));
    }

    private void write(final @NotNull ByteBuf buf) {
        writeResourceLocation(buf, this.id());
        writePayload(buf, this.data);
    }

    @Override
    public @NotNull String id() {
        return VelocityProxy.PLAYER_INFO_CHANNEL.toString();
    }

    @Override
    public @NotNull StreamCodec<@NotNull ByteBuf, @NotNull PlayerInfoQueryPayload> codec() {
        return STREAM_CODEC;
    }
}
