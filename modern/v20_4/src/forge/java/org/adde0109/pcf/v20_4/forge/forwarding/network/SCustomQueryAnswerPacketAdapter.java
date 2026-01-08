package org.adde0109.pcf.v20_4.forge.forwarding.network;

import dev.neuralnexus.taterapi.adapter.AdapterCodec;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryAnswerPayloadImpl;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;

import org.adde0109.pcf.v21_11.forwarding.network.MCQueryAnswerPayload;
import org.jspecify.annotations.NonNull;

@AConstraint(mappings = Mappings.SEARGE)
public final class SCustomQueryAnswerPacketAdapter
        implements AdapterCodec<
                net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket,
                ServerboundCustomQueryAnswerPacket> {
    public static final SCustomQueryAnswerPacketAdapter INSTANCE =
            new SCustomQueryAnswerPacketAdapter();

    @Override
    public @NonNull ServerboundCustomQueryAnswerPacket from(
            final net.minecraft.network.protocol.login.@NonNull ServerboundCustomQueryAnswerPacket
                    object) {
        if (object.payload() == null) {
            return new ServerboundCustomQueryAnswerPacket(object.transactionId());
        }
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        object.payload().write(buf);
        return new ServerboundCustomQueryAnswerPacket(
                object.transactionId(), new CustomQueryAnswerPayloadImpl(buf.slice()));
    }

    @Override
    public net.minecraft.network.protocol.login.@NonNull ServerboundCustomQueryAnswerPacket to(
            final @NonNull ServerboundCustomQueryAnswerPacket object) {
        if (object.payload() == null) {
            return new net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket(
                    object.transactionId(), null);
        }
        return new net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket(
                object.transactionId(), new MCQueryAnswerPayload(object.payload().data()));
    }
}
