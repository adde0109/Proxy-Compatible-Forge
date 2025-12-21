package org.adde0109.pcf.v7_10.forge.forwarding.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import org.adde0109.pcf.forwarding.network.CustomQueryAnswerPayload;
import org.adde0109.pcf.forwarding.network.ServerboundCustomQueryAnswerPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public final class C2SCustomQueryPacket extends Packet {
    private ServerboundCustomQueryAnswerPacket packet;

    public C2SCustomQueryPacket() {}

    public C2SCustomQueryPacket(final @NotNull ServerboundCustomQueryAnswerPacket packet) {
        this.packet = packet;
    }

    public C2SCustomQueryPacket(
            final int transactionId, final @Nullable CustomQueryAnswerPayload payload) {
        this.packet = new ServerboundCustomQueryAnswerPacket(transactionId, payload);
    }

    public C2SCustomQueryPacket(final int transactionId) {
        this.packet = new ServerboundCustomQueryAnswerPacket(transactionId);
    }

    @Override
    public void readPacketData(final @NotNull PacketBuffer buf) {
        this.packet = ServerboundCustomQueryAnswerPacket.read(buf);
    }

    @Override
    public void writePacketData(final @NotNull PacketBuffer buf) {
        this.packet.write(buf);
    }

    @Override
    public void processPacket(final @NotNull INetHandler handler) {
        ((ServerLoginQueryListener) handler).handleCustomQueryPacket(this);
    }

    public int transactionId() {
        return this.packet.transactionId();
    }

    public @Nullable CustomQueryAnswerPayload payload() {
        return this.packet.payload();
    }
}
