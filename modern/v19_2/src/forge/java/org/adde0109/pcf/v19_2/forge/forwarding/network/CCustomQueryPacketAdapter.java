package org.adde0109.pcf.v19_2.forge.forwarding.network;

import static org.adde0109.pcf.common.Identifier.identifier;

import net.minecraft.network.FriendlyByteBuf;

import org.adde0109.pcf.forwarding.network.ClientboundCustomQueryPacket;
import org.adde0109.pcf.forwarding.network.CustomQueryPayloadImpl;
import org.adde0109.pcf.forwarding.network.codec.adapter.AdapterCodec;
import org.jetbrains.annotations.NotNull;

public final class CCustomQueryPacketAdapter
        implements AdapterCodec<
                net.minecraft.network.protocol.login.ClientboundCustomQueryPacket,
                ClientboundCustomQueryPacket> {
    public static final CCustomQueryPacketAdapter INSTANCE = new CCustomQueryPacketAdapter();

    @Override
    public @NotNull ClientboundCustomQueryPacket fromMC(
            final @NotNull net.minecraft.network.protocol.login.ClientboundCustomQueryPacket
                            mcObject) {
        return new ClientboundCustomQueryPacket(
                mcObject.getTransactionId(),
                new CustomQueryPayloadImpl(
                        mcObject.getIdentifier().toString(), mcObject.getData().slice()));
    }

    @Override
    public @NotNull net.minecraft.network.protocol.login.ClientboundCustomQueryPacket toMC(
            final @NotNull ClientboundCustomQueryPacket object) {
        return new net.minecraft.network.protocol.login.ClientboundCustomQueryPacket(
                object.transactionId(),
                identifier(object.payload().id()),
                new FriendlyByteBuf(object.payload().data().slice()));
    }
}
