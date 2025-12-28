package org.adde0109.pcf.v12_2.forge.forwarding.network;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;

import org.adde0109.pcf.forwarding.network.CustomQueryAnswerPayload;
import org.adde0109.pcf.forwarding.network.ServerboundCustomQueryAnswerPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@SuppressWarnings({"RedundantThrows", "unused"})
public final class C2SCustomQueryAnswerPacket implements Packet<INetHandlerLoginServer> {
    private ServerboundCustomQueryAnswerPacket packet;

    public C2SCustomQueryAnswerPacket() {}

    public C2SCustomQueryAnswerPacket(final @NotNull ServerboundCustomQueryAnswerPacket packet) {
        this.packet = packet;
    }

    @Override
    public void readPacketData(final @NotNull PacketBuffer buf) throws IOException {
        this.packet = ServerboundCustomQueryAnswerPacket.STREAM_CODEC.decode(buf);
    }

    @Override
    public void writePacketData(final @NotNull PacketBuffer buf) throws IOException {
        ServerboundCustomQueryAnswerPacket.STREAM_CODEC.encode(buf, this.packet);
    }

    @Override
    public void processPacket(final @NotNull INetHandlerLoginServer handler) {
        ((ServerLoginQueryListener) handler).handleCustomQueryPacket(this);
    }

    public int transactionId() {
        return this.packet.transactionId();
    }

    public @Nullable CustomQueryAnswerPayload payload() {
        return this.packet.payload();
    }
}
