package org.adde0109.pcf.v16_5.forge.forwarding.network;

import static org.adde0109.pcf.common.Identifier.identifier;

import net.minecraft.network.FriendlyByteBuf;

import org.adde0109.pcf.forwarding.network.codec.adapter.AdapterCodec;
import org.adde0109.pcf.forwarding.network.protocol.login.ClientboundCustomQueryPacket;
import org.adde0109.pcf.forwarding.network.protocol.login.custom.CustomQueryPayloadImpl;
import org.adde0109.pcf.mixin.v16_5.forge.forwarding.modern.ClientboundCustomQueryPacketAccessor;
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
                ((ClientboundCustomQueryPacketAccessor) mcObject).pcf$getTransactionId(),
                new CustomQueryPayloadImpl(
                        ((ClientboundCustomQueryPacketAccessor) mcObject)
                                .pcf$getIdentifier()
                                .toString(),
                        ((ClientboundCustomQueryPacketAccessor) mcObject).pcf$getData().slice()));
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull net.minecraft.network.protocol.login.ClientboundCustomQueryPacket toMC(
            final @NotNull ClientboundCustomQueryPacket object) {
        net.minecraft.network.protocol.login.ClientboundCustomQueryPacket mcObject =
                new net.minecraft.network.protocol.login.ClientboundCustomQueryPacket();
        ((ClientboundCustomQueryPacketAccessor) mcObject)
                .pcf$setTransactionId(object.transactionId());
        ((ClientboundCustomQueryPacketAccessor) mcObject)
                .pcf$setIdentifier(identifier(object.payload().id()));
        ((ClientboundCustomQueryPacketAccessor) mcObject)
                .pcf$setData(new FriendlyByteBuf(object.payload().data().slice()));
        return mcObject;
    }
}
