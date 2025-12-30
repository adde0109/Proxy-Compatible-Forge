package org.adde0109.pcf.v16_5.forge.forwarding.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;

import org.adde0109.pcf.forwarding.network.CustomQueryAnswerPayloadImpl;
import org.adde0109.pcf.forwarding.network.ServerboundCustomQueryAnswerPacket;
import org.adde0109.pcf.forwarding.network.codec.adapter.AdapterCodec;
import org.adde0109.pcf.mixin.v16_5.forge.forwarding.modern.ServerboundCustomQueryPacketAccessor;
import org.jetbrains.annotations.NotNull;

public final class SCustomQueryAnswerPacketAdapter
        implements AdapterCodec<ServerboundCustomQueryPacket, ServerboundCustomQueryAnswerPacket> {
    public static final SCustomQueryAnswerPacketAdapter INSTANCE =
            new SCustomQueryAnswerPacketAdapter();

    @Override
    public @NotNull ServerboundCustomQueryAnswerPacket fromMC(
            final @NotNull ServerboundCustomQueryPacket mcObject) {
        if (((ServerboundCustomQueryPacketAccessor) mcObject).pcf$getData() == null) {
            return new ServerboundCustomQueryAnswerPacket(
                    ((ServerboundCustomQueryPacketAccessor) mcObject).pcf$getTransactionId());
        }
        return new ServerboundCustomQueryAnswerPacket(
                ((ServerboundCustomQueryPacketAccessor) mcObject).pcf$getTransactionId(),
                new CustomQueryAnswerPayloadImpl(
                        ((ServerboundCustomQueryPacketAccessor) mcObject).pcf$getData().slice()));
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
