package org.adde0109.pcf.v7_10.forge.forwarding.modern;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

@SuppressWarnings("unused")
public final class SDummyPacket extends Packet {
    public SDummyPacket() {}

    @Override
    public void readPacketData(PacketBuffer buf) {}

    @Override
    public void writePacketData(PacketBuffer buffer) {}

    @Override
    public void processPacket(INetHandler handler) {}
}
