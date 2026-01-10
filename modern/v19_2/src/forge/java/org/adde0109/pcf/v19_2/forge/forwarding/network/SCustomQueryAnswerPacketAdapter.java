package org.adde0109.pcf.v19_2.forge.forwarding.network;

import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryAnswerPayloadImpl;
import dev.neuralnexus.taterapi.serialization.Result;
import dev.neuralnexus.taterapi.serialization.codecs.ReversibleCodec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;

public final class SCustomQueryAnswerPacketAdapter
        implements ReversibleCodec<
                ServerboundCustomQueryPacket, ServerboundCustomQueryAnswerPacket> {
    public static final SCustomQueryAnswerPacketAdapter INSTANCE =
            new SCustomQueryAnswerPacketAdapter();

    @Override
    public Result<ServerboundCustomQueryAnswerPacket> encode(
            final ServerboundCustomQueryPacket object) {
        if (object.getData() == null) {
            return Result.success(
                    new ServerboundCustomQueryAnswerPacket(object.getTransactionId()));
        }
        return Result.success(
                new ServerboundCustomQueryAnswerPacket(
                        object.getTransactionId(),
                        new CustomQueryAnswerPayloadImpl(object.getData().slice())));
    }

    @Override
    public Result<ServerboundCustomQueryPacket> decode(
            final ServerboundCustomQueryAnswerPacket object) {
        if (object.payload() == null) {
            return Result.success(new ServerboundCustomQueryPacket(object.transactionId(), null));
        }
        return Result.success(
                new ServerboundCustomQueryPacket(
                        object.transactionId(),
                        new FriendlyByteBuf(object.payload().data().slice())));
    }
}
