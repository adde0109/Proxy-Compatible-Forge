package org.adde0109.pcf.forwarding.modern;

import static org.adde0109.pcf.common.Identifier.identifier;
import static org.adde0109.pcf.forwarding.modern.ReflectionUtils.V21_9;
import static org.adde0109.pcf.forwarding.modern.ReflectionUtils.getProperties;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.Crypt.MAX_KEY_SIGNATURE_SIZE;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readByteArray;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readInstant;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readNullable;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readOrElse;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readPublicKey;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readUtf;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readVarInt;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.forwarding.network.FriendlyByteBuf;
import org.adde0109.pcf.forwarding.network.protocol.login.custom.CustomQueryPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Adapted from <a
 * href="https://github.com/PaperMC/Paper/blob/main/paper-server/src/main/java/com/destroystokyo/paper/proxy/VelocityProxy.java">PaperMC</a>
 * <br>
 * While Velocity supports BungeeCord-style IP forwarding, it is not secure. Users have a lot of
 * problems setting up firewalls or setting up plugins like IPWhitelist. Further, the BungeeCord IP
 * forwarding protocol still retains essentially its original form, when there is brand-new support
 * for custom login plugin messages in 1.13.
 *
 * <p>Velocity's modern IP forwarding uses an HMAC-SHA256 code to ensure authenticity of messages,
 * is packed into a binary format that is smaller than BungeeCord's forwarding, and is integrated
 * into the Minecraft login process by using the 1.13 login plugin message packet.
 */
public final class VelocityProxy {
    public static final int MODERN_DEFAULT = 1;
    public static final int MODERN_FORWARDING_WITH_KEY = 2;
    public static final int MODERN_FORWARDING_WITH_KEY_V2 = 3;
    public static final int MODERN_LAZY_SESSION = 4;
    public static final byte MODERN_MAX_VERSION;
    public static final ByteBuf PLAYER_INFO_PACKET;
    public static final Object PLAYER_INFO_CHANNEL = identifier("velocity:player_info");

    static {
        final MinecraftVersion version = MetaAPI.instance().version();
        if (version.isOlderThan(MinecraftVersions.V19)) {
            MODERN_MAX_VERSION = MODERN_DEFAULT;
        } else if (version.is(MinecraftVersions.V19)) {
            MODERN_MAX_VERSION = MODERN_FORWARDING_WITH_KEY;
        } else if (version.isInRange(MinecraftVersions.V19_1, MinecraftVersions.V19_2)) {
            MODERN_MAX_VERSION = MODERN_FORWARDING_WITH_KEY_V2;
        } else if (version.isAtLeast(MinecraftVersions.V19_3)) {
            MODERN_MAX_VERSION = MODERN_LAZY_SESSION;
        } else {
            MODERN_MAX_VERSION = MODERN_DEFAULT;
        }
        if (version.isAtLeast(MinecraftVersions.V12)) {
            PLAYER_INFO_PACKET =
                    Unpooled.wrappedBuffer(new byte[] {MODERN_MAX_VERSION}).asReadOnly();
        } else {
            //noinspection deprecation
            PLAYER_INFO_PACKET =
                    Unpooled.unmodifiableBuffer(
                            Unpooled.wrappedBuffer(new byte[] {MODERN_MAX_VERSION}));
        }
        PCF.logger.debug("Velocity modern forwarding max version: " + MODERN_MAX_VERSION);
    }

    public static final CustomQueryPayload PLAYER_INFO_PAYLOAD =
            new PlayerInfoQueryPayload(PLAYER_INFO_PACKET);

    private static final String ALGORITHM = "HmacSHA256";

    private VelocityProxy() {}

    /**
     * Checks the integrity of the player forwarding response
     *
     * @param buf the buffer
     * @return true if the integrity is valid, false otherwise
     * @throws AssertionError if the algorithm is not found or the key is invalid
     */
    public static boolean checkIntegrity(final @NotNull ByteBuf buf) {
        final byte[] signature = new byte[32];
        buf.readBytes(signature);

        final byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), data);

        try {
            final Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(
                    new SecretKeySpec(
                            PCF.instance().forwarding().secret().getBytes(StandardCharsets.UTF_8),
                            ALGORITHM));
            final byte[] mySignature = mac.doFinal(data);
            if (!MessageDigest.isEqual(signature, mySignature)) {
                return false;
            }
        } catch (final InvalidKeyException | NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        return true;
    }

    /**
     * Creates a GameProfile from the given data
     *
     * @param playerId the player UUID
     * @param playerName the player name
     * @param buf the buffer
     * @return the GameProfile
     */
    public static @NotNull GameProfile createProfile(
            final @NotNull UUID playerId,
            final @NotNull String playerName,
            final @NotNull ByteBuf buf) {
        final GameProfile profile;
        if (V21_9.result()) {
            profile = new GameProfile(playerId, playerName, new PropertyMap(readProperties(buf)));
        } else {
            profile = new GameProfile(playerId, playerName);
            PropertyMap propertiesMap = getProperties(profile);
            final List<Map.Entry<String, Property>> properties = readProperties_7(buf);
            for (final Map.Entry<String, Property> entry : properties) {
                propertiesMap.put(entry.getKey(), entry.getValue());
            }
        }
        return profile;
    }

    /**
     * Reads profile properties from the given ByteBuf
     *
     * @param buf the buffer
     * @return a multimap of properties
     */
    private static @NotNull Multimap<String, Property> readProperties(final @NotNull ByteBuf buf) {
        final int count = readVarInt(buf);
        final ImmutableMultimap.Builder<String, Property> propertiesBuilder =
                ImmutableMultimap.builder();
        for (int i = 0; i < count; i++) {
            final String name = readUtf(buf);
            final String value = readUtf(buf);
            final @Nullable String signature = readNullable(buf, FriendlyByteBuf::readUtf);
            propertiesBuilder.put(name, new Property(name, value, signature));
        }
        return propertiesBuilder.build();
    }

    /**
     * Required for Forge 1.7.x due to classloader issues with Guava
     *
     * @param buf the buffer
     * @return a list of properties
     */
    private static @NotNull List<Map.Entry<String, Property>> readProperties_7(
            final @NotNull ByteBuf buf) {
        final int count = readVarInt(buf);
        final List<Map.Entry<String, Property>> properties = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final String name = readUtf(buf);
            final String value = readUtf(buf);
            final @Nullable String signature = readNullable(buf, FriendlyByteBuf::readUtf);
            properties.add(new SimpleImmutableEntry<>(name, new Property(name, value, signature)));
        }
        return properties;
    }

    /**
     * Modern Forwarding v3 - 1.19.1 - 1.19.2 <br>
     *
     * @param buf The ByteBuf to read from
     * @param orElse The UUID to return if no UUID is present
     * @return The UUID read from the ByteBuf, or the given UUID if none is present
     */
    public static @NotNull UUID readSignerUuidOrElse(
            final @NotNull ByteBuf buf, final @NotNull UUID orElse) {
        return readOrElse(buf, FriendlyByteBuf::readUUID, orElse);
    }

    /**
     * Modern Forwarding v2 - 1.19 - when using ProfilePublicKey::new <br>
     * Modern Forwarding v3 - 1.19.1 - 1.19.2 <br>
     *
     * @param buf The ByteBuf to read from
     * @return The ProfilePublicKey.Data read from the ByteBuf
     * @throws DecoderException If there was an error reading the key
     */
    public static @NotNull ProfilePublicKeyData readForwardedKey(final @NotNull ByteBuf buf)
            throws DecoderException {
        return new ProfilePublicKeyData(
                readInstant(buf), readPublicKey(buf), readByteArray(buf, MAX_KEY_SIGNATURE_SIZE));
    }
}
