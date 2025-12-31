package org.adde0109.pcf.v7_10.forge.forwarding.network;

import static org.adde0109.pcf.common.Identifier.identifier;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import org.adde0109.pcf.forwarding.network.protocol.login.ClientboundCustomQueryPacket;
import org.adde0109.pcf.forwarding.network.protocol.login.custom.CustomQueryPayload;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class S2CCustomQueryPacket extends Packet {
    private ClientboundCustomQueryPacket packet;

    public S2CCustomQueryPacket() {}

    public S2CCustomQueryPacket(final @NotNull ClientboundCustomQueryPacket packet) {
        this.packet = packet;
    }

    @Override
    public void readPacketData(@NotNull PacketBuffer buf) {
        this.packet = ClientboundCustomQueryPacket.STREAM_CODEC.decode(buf);
    }

    @Override
    public void writePacketData(@NotNull PacketBuffer buf) {
        ClientboundCustomQueryPacket.STREAM_CODEC.encode(buf, this.packet);
    }

    @Override
    public void processPacket(@NotNull INetHandler handler) {
        ((ClientLoginQueryListener) handler).handleCustomQuery(this);
    }

    public int transactionId() {
        return this.packet.transactionId();
    }

    public @NotNull ResourceLocation id() {
        return identifier(this.packet.payload().id());
    }

    public @NotNull CustomQueryPayload payload() {
        return this.packet.payload();
    }
}
