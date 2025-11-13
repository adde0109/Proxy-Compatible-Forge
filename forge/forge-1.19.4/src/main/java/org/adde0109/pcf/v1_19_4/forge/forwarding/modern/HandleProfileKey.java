package org.adde0109.pcf.v1_19_4.forge.forwarding.modern;

import static org.adde0109.pcf.common.FByteBuf.readByteArray;
import static org.adde0109.pcf.common.FByteBuf.readUUID;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_DEFAULT;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_FORWARDING_WITH_KEY;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_FORWARDING_WITH_KEY_V2;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.world.entity.player.ProfilePublicKey;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.mixin.v1_19.forge.forwarding.modern.ServerLoginPacketListenerImplAccessor_V1;
import org.adde0109.pcf.mixin.v1_19_2.forge.forwarding.modern.ServerLoginPacketListenerImplAccessor_V2;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.time.Instant;
import java.util.UUID;

public final class HandleProfileKey {
    private static final Logger LOGGER = LoggerFactory.getLogger("ServerLoginPacketListenerImpl");
    private static final boolean IS_19_1_2 =
            MetaAPI.instance()
                    .version()
                    .isInRange(MinecraftVersions.V19_1, MinecraftVersions.V19_2);
    private static final Component MISSING_PROFILE_PUBLIC_KEY =
            Component.translatable("multiplayer.disconnect.missing_public_key");
    private static final Component INVALID_SIGNATURE =
            Component.translatable("multiplayer.disconnect.invalid_public_key_signature");

    public static @Nullable Component handle(
            ServerLoginPacketListenerImpl self, FriendlyByteBuf buf, int version, UUID profileId) {

        // Clear key on 1.19.1 - 1.19.2 if using MODERN_DEFAULT
        if (version == MODERN_DEFAULT && IS_19_1_2) {
            ((ServerLoginPacketListenerImplAccessor_V2) self).pcf$setProfilePublicKeyData(null);
        }

        MinecraftServer server = (MinecraftServer) MetaAPI.instance().server();
        boolean enforceSecureProfile = server.enforceSecureProfile();

        // 1.19 forwarding with key v1
        if (version == MODERN_FORWARDING_WITH_KEY) {
            try {
                ProfilePublicKey publicKey = readKey(buf);
                if (enforceSecureProfile && publicKey == null) {
                    return MISSING_PROFILE_PUBLIC_KEY;
                }
                ((ServerLoginPacketListenerImplAccessor_V1) self)
                        .pcf$setPlayerProfilePublicKey(publicKey);
            } catch (CryptException e) {
                PCF.logger.error("Public key read failed.", e);
                if (enforceSecureProfile) {
                    return INVALID_SIGNATURE;
                }
            }
        }

        // 1.19.1 - 1.19.2 forwarding with key v2
        if (version == MODERN_FORWARDING_WITH_KEY_V2) {
            final ProfilePublicKey.Data forwardedKeyData = readForwardedKey(buf);
            final UUID signer = readSignerUuidOrElse(buf, profileId);
            if (((ServerLoginPacketListenerImplAccessor_V2) self).pcf$profilePublicKeyData()
                    == null) {
                try {
                    ServerLoginPacketListenerImplAccessor_V2.pcf$validatePublicKey(
                            forwardedKeyData,
                            signer,
                            server.getServiceSignatureValidator(),
                            enforceSecureProfile);
                    ((ServerLoginPacketListenerImplAccessor_V2) self)
                            .pcf$setProfilePublicKeyData(forwardedKeyData);
                } catch (Exception e) {
                    // net.minecraft.world.entity.player.ProfilePublicKey.ValidationException
                    LOGGER.error("Failed to validate profile key: {}", e.getMessage());
                    return INVALID_SIGNATURE; // e.getComponent();
                }
            }
        }
        return null;
    }

    /**
     * Adapted from: <br>
     * <a
     * href="https://github.com/PaperMC/Paper-archive/blob/ver/1.19.4/patches/server/0874-Add-Velocity-IP-Forwarding-Support.patch">Paper
     * 1.19.4</a>
     */
    private static final int MAX_KEY_SIGNATURE_SIZE = 4096;

    public static UUID readSignerUuidOrElse(final ByteBuf buf, final UUID orElse) {
        return buf.readBoolean() ? readUUID(buf) : orElse;
    }

    private static ProfilePublicKey.Data readForwardedKey(final FriendlyByteBuf buf) {
        return new ProfilePublicKey.Data(buf);
    }

    private static ProfilePublicKey readKey(final ByteBuf buf) throws CryptException {
        Instant expiry = Instant.ofEpochMilli(buf.readLong());
        PublicKey key = Crypt.byteToPublicKey(readByteArray(buf, 512));
        byte[] signature = readByteArray(buf, MAX_KEY_SIGNATURE_SIZE);
        return new ProfilePublicKey(new ProfilePublicKey.Data(expiry, key, signature));
    }
}
