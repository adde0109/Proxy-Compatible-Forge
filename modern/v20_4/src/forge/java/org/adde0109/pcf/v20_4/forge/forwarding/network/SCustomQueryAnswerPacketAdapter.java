package org.adde0109.pcf.v20_4.forge.forwarding.network;

import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryAnswerPayloadImpl;
import dev.neuralnexus.taterapi.serialization.Result;
import dev.neuralnexus.taterapi.serialization.codecs.ReversibleCodec;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;

import org.adde0109.pcf.v21_11.forwarding.network.MCQueryAnswerPayload;

public final class SCustomQueryAnswerPacketAdapter
        implements ReversibleCodec<
                net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket,
                ServerboundCustomQueryAnswerPacket> {
    public static final SCustomQueryAnswerPacketAdapter INSTANCE =
            new SCustomQueryAnswerPacketAdapter();

    @Override
    public Result<ServerboundCustomQueryAnswerPacket> encode(
            final net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket object) {
        if (object.payload() == null) {
            return Result.success(new ServerboundCustomQueryAnswerPacket(object.transactionId()));
        }
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        object.payload().write(buf);
        return Result.success(
                new ServerboundCustomQueryAnswerPacket(
                        object.transactionId(), new CustomQueryAnswerPayloadImpl(buf.slice())));
    }

    @Override
    public Result<net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket> decode(
            final ServerboundCustomQueryAnswerPacket object) {
        if (object.payload() == null) {
            return Result.success(
                    new net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket(
                            object.transactionId(), null));
        }
        return Result.success(
                new net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket(
                        object.transactionId(), new MCQueryAnswerPayload(object.payload().data())));
    }
}
