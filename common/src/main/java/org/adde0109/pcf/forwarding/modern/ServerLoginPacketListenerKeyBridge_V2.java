package org.adde0109.pcf.forwarding.modern;

import org.jetbrains.annotations.Nullable;

public interface ServerLoginPacketListenerKeyBridge_V2 {
    @Nullable VelocityProxy.ProfilePublicKeyData bridge$profilePublicKeyData();

    void bridge$profilePublicKeyData(
            final @Nullable VelocityProxy.ProfilePublicKeyData publicKeyData);

    void bridge$validatePublicKey(
            final @Nullable VelocityProxy.ProfilePublicKeyData keyData,
            final @Nullable java.util.UUID signer)
            throws Exception;
}
