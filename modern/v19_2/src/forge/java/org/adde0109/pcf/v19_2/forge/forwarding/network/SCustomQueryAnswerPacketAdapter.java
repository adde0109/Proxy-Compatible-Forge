package org.adde0109.pcf.v19_2.forge.forwarding.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;

import org.adde0109.pcf.forwarding.network.CustomQueryAnswerPayloadImpl;
import org.adde0109.pcf.forwarding.network.ServerboundCustomQueryAnswerPacket;
import org.adde0109.pcf.forwarding.network.codec.adapter.AdapterCodec;
import org.jetbrains.annotations.NotNull;

public final class SCustomQueryAnswerPacketAdapter
        implements AdapterCodec<ServerboundCustomQueryPacket, ServerboundCustomQueryAnswerPacket> {
    public static final SCustomQueryAnswerPacketAdapter INSTANCE =
            new SCustomQueryAnswerPacketAdapter();

    @Override
    public @NotNull ServerboundCustomQueryAnswerPacket fromMC(
            final @NotNull ServerboundCustomQueryPacket mcObject) {
        if (mcObject.getData() == null) {
            return new ServerboundCustomQueryAnswerPacket(mcObject.getTransactionId());
        }
        return new ServerboundCustomQueryAnswerPacket(
                mcObject.getTransactionId(),
                new CustomQueryAnswerPayloadImpl(mcObject.getData().slice()));
    }

    @Override
    public @NotNull ServerboundCustomQueryPacket toMC(
            final @NotNull ServerboundCustomQueryAnswerPacket object) {
        if (object.payload() == null) {
            return new ServerboundCustomQueryPacket(object.transactionId(), null);
        }
        return new ServerboundCustomQueryPacket(
                object.transactionId(), new FriendlyByteBuf(object.payload().data().slice()));
    }
}
