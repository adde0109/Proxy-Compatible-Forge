package org.adde0109.pcf.forwarding.network;

import static org.adde0109.pcf.common.FriendlyByteBuf.readVarInt;
import static org.adde0109.pcf.common.FriendlyByteBuf.writeVarInt;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.forwarding.network.codec.StreamCodec;
import org.adde0109.pcf.forwarding.network.codec.adapter.AdapterCodec;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public record ClientboundCustomQueryPacket(int transactionId, @NotNull CustomQueryPayload payload)
        implements Packet {
    public static final StreamCodec<ByteBuf, ClientboundCustomQueryPacket> STREAM_CODEC =
            Packet.codec(ClientboundCustomQueryPacket::write, ClientboundCustomQueryPacket::read);

    public static final AdapterCodec<?, ClientboundCustomQueryPacket> ADAPTER_CODEC =
            (AdapterCodec<?, ClientboundCustomQueryPacket>)
                    PCF.instance().adapters().toMC(ClientboundCustomQueryPacket.class);

    private static ClientboundCustomQueryPacket read(final @NotNull ByteBuf buf) {
        final int transactionId = readVarInt(buf);
        final CustomQueryPayload p = CustomQueryPayload.DEFAULT_CODEC.decode(buf);
        return new ClientboundCustomQueryPacket(transactionId, p);
    }

    private void write(final @NotNull ByteBuf buf) {
        writeVarInt(buf, this.transactionId);
        ((StreamCodec<ByteBuf, CustomQueryPayload>) this.payload.codec()).encode(buf, this.payload);
    }

    public static <T> @NotNull ClientboundCustomQueryPacket fromMC(final @NotNull T obj) {
        return ((AdapterCodec<T, ClientboundCustomQueryPacket>) ADAPTER_CODEC).fromMC(obj);
    }

    public <T> @NotNull T toMC() {
        return ((AdapterCodec<T, ClientboundCustomQueryPacket>) ADAPTER_CODEC).toMC(this);
    }
}
