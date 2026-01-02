package org.adde0109.pcf.v16_5.forge.forwarding.network;

import static dev.neuralnexus.taterapi.resources.Identifier.identifier;

import dev.neuralnexus.taterapi.adapter.AdapterCodec;
import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayloadImpl;

import net.minecraft.network.FriendlyByteBuf;

import org.adde0109.pcf.mixin.v16_5.forge.forwarding.modern.ClientboundCustomQueryPacketAccessor;
import org.jetbrains.annotations.NotNull;

public final class CCustomQueryPacketAdapter
        implements AdapterCodec<
                net.minecraft.network.protocol.login.ClientboundCustomQueryPacket,
                ClientboundCustomQueryPacket> {
    public static final CCustomQueryPacketAdapter INSTANCE = new CCustomQueryPacketAdapter();

    @Override
    public @NotNull ClientboundCustomQueryPacket from(
            final @NotNull net.minecraft.network.protocol.login.ClientboundCustomQueryPacket
                            object) {
        return new ClientboundCustomQueryPacket(
                ((ClientboundCustomQueryPacketAccessor) object).pcf$getTransactionId(),
                new CustomQueryPayloadImpl(
                        ((ClientboundCustomQueryPacketAccessor) object)
                                .pcf$getIdentifier()
                                .toString(),
                        ((ClientboundCustomQueryPacketAccessor) object).pcf$getData().slice()));
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull net.minecraft.network.protocol.login.ClientboundCustomQueryPacket to(
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
