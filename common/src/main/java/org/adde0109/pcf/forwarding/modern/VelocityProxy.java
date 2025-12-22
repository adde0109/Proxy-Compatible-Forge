package org.adde0109.pcf.forwarding.modern;

import static org.adde0109.pcf.common.FByteBuf.readNullable;
import static org.adde0109.pcf.common.FByteBuf.readUUID;
import static org.adde0109.pcf.common.FByteBuf.readUtf;
import static org.adde0109.pcf.common.FByteBuf.readVarInt;
import static org.adde0109.pcf.common.Identifier.identifier;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.FByteBuf;
import org.adde0109.pcf.forwarding.network.CustomQueryPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public static final @NotNull ByteBuf PLAYER_INFO_PACKET;

    private static final @NotNull Object player_info_channel = identifier("velocity:player_info");

    @SuppressWarnings("unchecked")
    public static <T> @NotNull T PLAYER_INFO_CHANNEL() {
        return (T) player_info_channel;
    }

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

    public static final @NotNull CustomQueryPayload PLAYER_INFO_PAYLOAD =
            new PlayerInfoChannelPayload("velocity:player_info", PLAYER_INFO_PACKET);

    private static final String ALGORITHM = "HmacSHA256";

    private static Method propertiesMethod;

    private VelocityProxy() {}

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

    @SuppressWarnings("JavaReflectionMemberAccess")
    public static @NotNull GameProfile createProfile(final @NotNull ByteBuf buf) {
        final GameProfile profile;
        if (Constraint.builder()
                .min(MinecraftVersions.V21_9)
                .build()
                .result()) { // com.mojang:authlib:7.0.0 or newer
            profile =
                    new GameProfile(
                            readUUID(buf), readUtf(buf, 16), new PropertyMap(readProperties(buf)));
        } else {
            profile = new GameProfile(readUUID(buf), readUtf(buf, 16));
            try {
                if (propertiesMethod == null) {
                    propertiesMethod = GameProfile.class.getMethod("getProperties");
                }
                PropertyMap propertiesMap = (PropertyMap) propertiesMethod.invoke(profile);
                final List<Map.Entry<String, Property>> properties = readProperties_7(buf);
                for (final Map.Entry<String, Property> entry : properties) {
                    propertiesMap.put(entry.getKey(), entry.getValue());
                }
            } catch (final IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException e) {
                throw new IllegalStateException("Failed to set properties on GameProfile", e);
            }
        }
        return profile;
    }

    private static @NotNull Multimap<String, Property> readProperties(final @NotNull ByteBuf buf) {
        final int count = readVarInt(buf);
        final ImmutableMultimap.Builder<String, Property> propertiesBuilder =
                ImmutableMultimap.builder();
        for (int i = 0; i < count; i++) {
            final String name = readUtf(buf);
            final String value = readUtf(buf);
            final @Nullable String signature = readNullable(buf, FByteBuf::readUtf);
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
            final @Nullable String signature = readNullable(buf, FByteBuf::readUtf);
            properties.add(new SimpleImmutableEntry<>(name, new Property(name, value, signature)));
        }
        return properties;
    }
}
