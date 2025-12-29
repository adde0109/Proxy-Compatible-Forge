package org.adde0109.pcf.v19_2.forge.forwarding.modern;

import net.minecraft.world.entity.player.ProfilePublicKey;

import org.adde0109.pcf.forwarding.modern.VelocityProxy.ProfilePublicKeyData;
import org.adde0109.pcf.forwarding.network.codec.adapter.AdapterCodec;
import org.jetbrains.annotations.NotNull;

public final class ProfilePublicKeyDataAdapter
        implements AdapterCodec<ProfilePublicKey.Data, ProfilePublicKeyData> {
    public static final ProfilePublicKeyDataAdapter INSTANCE = new ProfilePublicKeyDataAdapter();

    @Override
    public @NotNull ProfilePublicKeyData fromMC(@NotNull ProfilePublicKey.Data mcObject) {
        return new ProfilePublicKeyData(
                mcObject.expiresAt(), mcObject.key(), mcObject.keySignature());
    }

    @Override
    public @NotNull ProfilePublicKey.Data toMC(@NotNull ProfilePublicKeyData object) {
        return new ProfilePublicKey.Data(object.expiresAt(), object.key(), object.keySignature());
    }
}
