package org.adde0109.pcf.v1_20_2.neoforge.forwarding.modern;

import net.minecraft.network.FriendlyByteBuf;

import org.jetbrains.annotations.NotNull;

/** Dummy interface so QueryAnswerPayload can work in Searge-mapped environments. */
public sealed interface QueryAnswerPayloadSearge permits QueryAnswerPayload {
    void write(final @NotNull FriendlyByteBuf buf);

    @SuppressWarnings("unused")
    default void m_295630_(final @NotNull FriendlyByteBuf buf) {
        this.write(buf);
    }
}
