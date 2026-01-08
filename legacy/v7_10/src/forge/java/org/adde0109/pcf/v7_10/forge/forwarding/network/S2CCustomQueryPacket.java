package org.adde0109.pcf.v7_10.forge.forwarding.network;

import static dev.neuralnexus.taterapi.resources.Identifier.identifier;

import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayload;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public final class S2CCustomQueryPacket extends Packet {
    private ClientboundCustomQueryPacket packet;

    public S2CCustomQueryPacket() {}

    public S2CCustomQueryPacket(final @NonNull ClientboundCustomQueryPacket packet) {
        this.packet = packet;
    }

    @Override
    public void readPacketData(@NonNull PacketBuffer buf) {
        this.packet = ClientboundCustomQueryPacket.STREAM_CODEC.decode(buf);
    }

    @Override
    public void writePacketData(@NonNull PacketBuffer buf) {
        ClientboundCustomQueryPacket.STREAM_CODEC.encode(buf, this.packet);
    }

    @Override
    public void processPacket(@NonNull INetHandler handler) {
        ((ClientLoginQueryListener) handler).handleCustomQuery(this);
    }

    public int transactionId() {
        return this.packet.transactionId();
    }

    public @NonNull ResourceLocation id() {
        return identifier(this.packet.payload().id());
    }

    public @NonNull CustomQueryPayload payload() {
        return this.packet.payload();
    }
}
