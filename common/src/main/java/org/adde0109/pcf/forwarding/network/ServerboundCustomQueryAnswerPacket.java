package org.adde0109.pcf.forwarding.network;

import static org.adde0109.pcf.common.FByteBuf.readNullable;
import static org.adde0109.pcf.common.FByteBuf.readPayload;
import static org.adde0109.pcf.common.FByteBuf.readVarInt;
import static org.adde0109.pcf.common.FByteBuf.writeNullable;
import static org.adde0109.pcf.common.FByteBuf.writeVarInt;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.forwarding.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ServerboundCustomQueryAnswerPacket(
        int transactionId, @Nullable CustomQueryAnswerPayload payload) implements Packet {
    public static final StreamCodec<ByteBuf, ServerboundCustomQueryAnswerPacket> STREAM_CODEC =
            Packet.codec(
                    ServerboundCustomQueryAnswerPacket::write,
                    ServerboundCustomQueryAnswerPacket::read);

    public ServerboundCustomQueryAnswerPacket(final int transactionId) {
        this(transactionId, null);
    }

    private static ServerboundCustomQueryAnswerPacket read(final @NotNull ByteBuf buf) {
        return new ServerboundCustomQueryAnswerPacket(
                readVarInt(buf),
                readNullable(buf, b -> new CustomQueryAnswerPayloadImpl(readPayload(b))));
    }

    private void write(final @NotNull ByteBuf buf) {
        writeVarInt(buf, this.transactionId);
        writeNullable(buf, this.payload, (b, p) -> p.write(b));
    }
}
