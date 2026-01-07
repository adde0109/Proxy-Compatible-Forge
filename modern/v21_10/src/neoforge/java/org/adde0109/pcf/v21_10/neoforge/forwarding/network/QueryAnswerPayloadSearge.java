package org.adde0109.pcf.v21_10.neoforge.forwarding.network;

import net.minecraft.network.FriendlyByteBuf;

import org.jspecify.annotations.NonNull;

/** Dummy interface so QueryAnswerPayload can work in Searge-mapped environments. */
public sealed interface QueryAnswerPayloadSearge permits MCQueryAnswerPayload {
    void write(final @NonNull FriendlyByteBuf buf);

    @SuppressWarnings("unused")
    default void m_295630_(final @NonNull FriendlyByteBuf buf) {
        this.write(buf);
    }
}
