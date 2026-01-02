package org.adde0109.pcf.v19_2.forge.forwarding.network;

import static dev.neuralnexus.taterapi.resources.Identifier.identifier;

import dev.neuralnexus.taterapi.adapter.AdapterCodec;
import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayloadImpl;

import net.minecraft.network.FriendlyByteBuf;

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
                object.getTransactionId(),
                new CustomQueryPayloadImpl(
                        object.getIdentifier().toString(), object.getData().slice()));
    }

    @Override
    public @NotNull net.minecraft.network.protocol.login.ClientboundCustomQueryPacket to(
            final @NotNull ClientboundCustomQueryPacket object) {
        return new net.minecraft.network.protocol.login.ClientboundCustomQueryPacket(
                object.transactionId(),
                identifier(object.payload().id()),
                new FriendlyByteBuf(object.payload().data().slice()));
    }
}
