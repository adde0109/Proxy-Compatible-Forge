package org.adde0109.pcf.v19_2.forge.forwarding.network;

import dev.neuralnexus.taterapi.adapter.AdapterCodec;
import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryAnswerPayloadImpl;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;

import org.jspecify.annotations.NonNull;

public final class SCustomQueryAnswerPacketAdapter
        implements AdapterCodec<ServerboundCustomQueryPacket, ServerboundCustomQueryAnswerPacket> {
    public static final SCustomQueryAnswerPacketAdapter INSTANCE =
            new SCustomQueryAnswerPacketAdapter();

    @Override
    public @NonNull ServerboundCustomQueryAnswerPacket from(
            final @NonNull ServerboundCustomQueryPacket object) {
        if (object.getData() == null) {
            return new ServerboundCustomQueryAnswerPacket(object.getTransactionId());
        }
        return new ServerboundCustomQueryAnswerPacket(
                object.getTransactionId(),
                new CustomQueryAnswerPayloadImpl(object.getData().slice()));
    }

    @Override
    public @NonNull ServerboundCustomQueryPacket to(
            final @NonNull ServerboundCustomQueryAnswerPacket object) {
        if (object.payload() == null) {
            return new ServerboundCustomQueryPacket(object.transactionId(), null);
        }
        return new ServerboundCustomQueryPacket(
                object.transactionId(), new FriendlyByteBuf(object.payload().data().slice()));
    }
}
