package org.adde0109.pcf.forwarding.network;

import static org.adde0109.pcf.common.FriendlyByteBuf.readNullable;
import static org.adde0109.pcf.common.FriendlyByteBuf.readVarInt;
import static org.adde0109.pcf.common.FriendlyByteBuf.writeNullable;
import static org.adde0109.pcf.common.FriendlyByteBuf.writeVarInt;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.forwarding.network.codec.StreamCodec;
import org.adde0109.pcf.forwarding.network.codec.adapter.AdapterCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public record ServerboundCustomQueryAnswerPacket(
        int transactionId, @Nullable CustomQueryAnswerPayload payload) implements Packet {
    public static final StreamCodec<ByteBuf, ServerboundCustomQueryAnswerPacket> STREAM_CODEC =
            Packet.codec(
                    ServerboundCustomQueryAnswerPacket::write,
                    ServerboundCustomQueryAnswerPacket::read);
    public static final AdapterCodec<?, ServerboundCustomQueryAnswerPacket> ADAPTER_CODEC =
            (AdapterCodec<?, ServerboundCustomQueryAnswerPacket>)
                    PCF.instance().adapters().toMC(ServerboundCustomQueryAnswerPacket.class);

    public ServerboundCustomQueryAnswerPacket(final int transactionId) {
        this(transactionId, null);
    }

    private static ServerboundCustomQueryAnswerPacket read(final @NotNull ByteBuf buf) {
        final int transactionId = readVarInt(buf);
        final CustomQueryAnswerPayload p =
                readNullable(buf, CustomQueryAnswerPayload.DEFAULT_CODEC);
        return new ServerboundCustomQueryAnswerPacket(transactionId, p);
    }

    @SuppressWarnings("DataFlowIssue")
    private void write(final @NotNull ByteBuf buf) {
        writeVarInt(buf, this.transactionId);
        writeNullable(
                buf,
                this.payload,
                ((StreamCodec<ByteBuf, CustomQueryAnswerPayload>) this.payload.codec()));
    }

    public static <T> @NotNull ServerboundCustomQueryAnswerPacket fromMC(final @NotNull T obj) {
        return ((AdapterCodec<T, ServerboundCustomQueryAnswerPacket>) ADAPTER_CODEC).fromMC(obj);
    }

    public <T> @NotNull T toMC() {
        return ((AdapterCodec<T, ServerboundCustomQueryAnswerPacket>) ADAPTER_CODEC).toMC(this);
    }
}
