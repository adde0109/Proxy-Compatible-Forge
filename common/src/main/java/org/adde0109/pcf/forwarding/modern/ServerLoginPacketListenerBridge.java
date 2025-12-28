package org.adde0109.pcf.forwarding.modern;

import com.mojang.authlib.GameProfile;

import org.jetbrains.annotations.NotNull;

public interface ServerLoginPacketListenerBridge {
    int bridge$velocityLoginMessageId();

    void bridge$setVelocityLoginMessageId(final int id);

    @NotNull ConnectionBridge bridge$connection();

    void bridge$disconnect(final @NotNull Object reason);

    void bridge$startClientVerification(final @NotNull GameProfile profile);

    void bridge$logger_info(final @NotNull String text, final Object... params);

    void bridge$logger_error(final @NotNull String text, final Object... params);
}
