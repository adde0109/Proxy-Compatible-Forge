package org.adde0109.pcf.v7_10.forge.forwarding.network;

import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryAnswerPayload;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("unused")
public final class C2SCustomQueryAnswerPacket extends Packet {
    private ServerboundCustomQueryAnswerPacket packet;

    public C2SCustomQueryAnswerPacket() {}

    public C2SCustomQueryAnswerPacket(final @NonNull ServerboundCustomQueryAnswerPacket packet) {
        this.packet = packet;
    }

    @Override
    public void readPacketData(final @NonNull PacketBuffer buf) {
        this.packet = ServerboundCustomQueryAnswerPacket.STREAM_CODEC.decode(buf);
    }

    @Override
    public void writePacketData(final @NonNull PacketBuffer buf) {
        ServerboundCustomQueryAnswerPacket.STREAM_CODEC.encode(buf, this.packet);
    }

    @Override
    public void processPacket(final @NonNull INetHandler handler) {
        ((ServerLoginQueryListener) handler).handleCustomQueryPacket(this);
    }

    public int transactionId() {
        return this.packet.transactionId();
    }

    public @Nullable CustomQueryAnswerPayload payload() {
        return this.packet.payload();
    }
}
