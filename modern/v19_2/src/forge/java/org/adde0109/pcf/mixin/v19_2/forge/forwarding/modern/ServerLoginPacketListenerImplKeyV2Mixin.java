package org.adde0109.pcf.mixin.v19_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;

import org.adde0109.pcf.forwarding.modern.ProfilePublicKeyData;
import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@AConstraint(
        mappings = Mappings.SEARGE,
        version = @Versions(min = MinecraftVersion.V19_1, max = MinecraftVersion.V19_2))
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplKeyV2Mixin
        implements ServerLoginPacketListenerBridge.KeyV2 {
    // spotless:off
    @Shadow private ProfilePublicKey.Data profilePublicKeyData;

    // ProfilePublicKey.ValidationException Doesn't exist on 1.19.1
    @SuppressWarnings("RedundantThrows")
    @Shadow private static ProfilePublicKey validatePublicKey(
            ProfilePublicKey.Data keyData,
            UUID signer,
            SignatureValidator validator,
            boolean enforceSecureProfile)
            throws Exception {
        throw new UnsupportedOperationException();
    }
    // spotless:on

    @Override
    public @Nullable ProfilePublicKeyData bridge$profilePublicKeyData() {
        if (this.profilePublicKeyData == null) {
            return null;
        }
        return ProfilePublicKeyData.fromMC(this.profilePublicKeyData);
    }

    @Override
    public void bridge$setProfilePublicKeyData(final @Nullable ProfilePublicKeyData publicKeyData) {
        if (publicKeyData == null) {
            this.profilePublicKeyData = null;
        } else {
            this.profilePublicKeyData = publicKeyData.toMC();
        }
    }

    @Override
    public void bridge$validatePublicKey(
            final @Nullable ProfilePublicKeyData keyData, final @Nullable UUID signer)
            throws Exception {
        MinecraftServer server = (MinecraftServer) MetaAPI.instance().server();
        validatePublicKey(
                keyData != null ? keyData.toMC() : null,
                signer != null ? signer : UUID.randomUUID(),
                server.getServiceSignatureValidator(),
                server.enforceSecureProfile());
    }
}
