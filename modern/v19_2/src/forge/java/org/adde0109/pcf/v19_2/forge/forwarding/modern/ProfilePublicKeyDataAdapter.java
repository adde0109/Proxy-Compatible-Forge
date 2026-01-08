package org.adde0109.pcf.v19_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.adapter.AdapterCodec;

import net.minecraft.world.entity.player.ProfilePublicKey;

import org.adde0109.pcf.forwarding.modern.ProfilePublicKeyData;
import org.jspecify.annotations.NonNull;

public final class ProfilePublicKeyDataAdapter
        implements AdapterCodec<ProfilePublicKey.Data, ProfilePublicKeyData> {
    public static final ProfilePublicKeyDataAdapter INSTANCE = new ProfilePublicKeyDataAdapter();

    @Override
    public @NonNull ProfilePublicKeyData from(ProfilePublicKey.@NonNull Data object) {
        return new ProfilePublicKeyData(object.expiresAt(), object.key(), object.keySignature());
    }

    @Override
    public ProfilePublicKey.@NonNull Data to(@NonNull ProfilePublicKeyData object) {
        return new ProfilePublicKey.Data(object.expiresAt(), object.key(), object.keySignature());
    }
}
