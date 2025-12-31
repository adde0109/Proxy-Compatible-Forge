package org.adde0109.pcf.forwarding.modern;

import com.mojang.authlib.GameProfile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ServerLoginPacketListenerBridge {
    int bridge$velocityLoginMessageId();

    void bridge$setVelocityLoginMessageId(final int id);

    @NotNull ConnectionBridge bridge$connection();

    void bridge$disconnect(final @NotNull Object reason);

    void bridge$startClientVerification(final @NotNull GameProfile profile);

    void bridge$logger_info(final @NotNull String text, final Object... params);

    void bridge$logger_error(final @NotNull String text, final Object... params);

    interface KeyV1 {
        void bridge$setPlayerProfilePublicKey(@Nullable ProfilePublicKeyData publicKeyData);
    }

    interface KeyV2 {
        @Nullable ProfilePublicKeyData bridge$profilePublicKeyData();

        void bridge$setProfilePublicKeyData(final @Nullable ProfilePublicKeyData publicKeyData);

        void bridge$validatePublicKey(
                final @Nullable ProfilePublicKeyData keyData, final @Nullable java.util.UUID signer)
                throws Exception;
    }
}
