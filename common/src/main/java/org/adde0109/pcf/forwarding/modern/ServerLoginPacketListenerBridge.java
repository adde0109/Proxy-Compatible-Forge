package org.adde0109.pcf.forwarding.modern;

import com.mojang.authlib.GameProfile;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface ServerLoginPacketListenerBridge {
    int bridge$velocityLoginMessageId();

    void bridge$setVelocityLoginMessageId(final int id);

    @NonNull ConnectionBridge bridge$connection();

    void bridge$disconnect(final @NonNull Object reason);

    void bridge$startClientVerification(final @NonNull GameProfile profile);

    void bridge$logger_info(final @NonNull String text, final Object... params);

    void bridge$logger_error(final @NonNull String text, final Object... params);

    interface KeyV1 {
        void bridge$setPlayerProfilePublicKey(@Nullable ProfilePublicKeyData publicKeyData);
    }

    interface KeyV2 {
        @Nullable ProfilePublicKeyData bridge$profilePublicKeyData();

        void bridge$setProfilePublicKeyData(final @Nullable ProfilePublicKeyData publicKeyData);

        void bridge$validatePublicKey(
                final @Nullable ProfilePublicKeyData keyData, final @Nullable UUID signer)
                throws Exception;
    }
}
