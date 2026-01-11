package org.adde0109.pcf.v19_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.serialization.Result;
import dev.neuralnexus.taterapi.serialization.codecs.ReversibleCodec;

import net.minecraft.world.entity.player.ProfilePublicKey;

import org.adde0109.pcf.forwarding.modern.ProfilePublicKeyData;

public final class ProfilePublicKeyDataAdapter
        implements ReversibleCodec<ProfilePublicKey.Data, ProfilePublicKeyData> {
    public static final ProfilePublicKeyDataAdapter INSTANCE = new ProfilePublicKeyDataAdapter();

    @Override
    public Result<ProfilePublicKeyData> encode(final ProfilePublicKey.Data object) {
        return Result.success(
                new ProfilePublicKeyData(object.expiresAt(), object.key(), object.keySignature()));
    }

    @Override
    public Result<ProfilePublicKey.Data> decode(final ProfilePublicKeyData object) {
        return Result.success(
                new ProfilePublicKey.Data(object.expiresAt(), object.key(), object.keySignature()));
    }
}
