package org.adde0109.pcf.forwarding.modern;

import static org.adde0109.pcf.common.FByteBuf.readUUID;
import static org.adde0109.pcf.common.FByteBuf.readUtf;
import static org.adde0109.pcf.common.FByteBuf.readVarInt;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.InetAddresses;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.adde0109.pcf.PCF;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
    private static final int MODERN_LAZY_SESSION = 4;
    public static final byte MODERN_MAX_VERSION;
    public static final ByteBuf PLAYER_INFO_PACKET;

    static {
        final MinecraftVersion version = MetaAPI.instance().version();
        if (version.isOlderThan(MinecraftVersions.V19)) {
            MODERN_MAX_VERSION = MODERN_DEFAULT;
        } else if (version.is(MinecraftVersions.V19)) {
            MODERN_MAX_VERSION = MODERN_FORWARDING_WITH_KEY;
        } else if (version.isInRange(MinecraftVersions.V19_1, MinecraftVersions.V19_2)) {
            // TODO: See if this can be used with server switching
            MODERN_MAX_VERSION = MODERN_FORWARDING_WITH_KEY_V2;
        } else if (version.isAtLeast(MinecraftVersions.V19_3)) {
            MODERN_MAX_VERSION = MODERN_LAZY_SESSION;
        } else {
            MODERN_MAX_VERSION = MODERN_DEFAULT;
        }
        PLAYER_INFO_PACKET = Unpooled.wrappedBuffer(new byte[] {MODERN_MAX_VERSION}).asReadOnly();
        PCF.logger.debug("Velocity modern forwarding max version: " + MODERN_MAX_VERSION);
    }

    private static final String ALGORITHM = "HmacSHA256";

    private static final boolean isAtLeast21_9 =
            MetaAPI.instance().version().isAtLeast(MinecraftVersions.V21_9);
    private static Method propertiesMethod;

    private VelocityProxy() {}

    public static boolean checkIntegrity(final ByteBuf buf) {
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

    public static InetAddress readAddress(final ByteBuf buf) {
        return InetAddresses.forString(readUtf(buf));
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    public static @NotNull GameProfile createProfile(final ByteBuf buf) {
        final GameProfile profile;
        if (isAtLeast21_9) { // com.mojang:authlib:7.0.0 or newer
            profile =
                    new GameProfile(
                            readUUID(buf), readUtf(buf, 16), new PropertyMap(readProperties(buf)));
        } else {
            profile = new GameProfile(readUUID(buf), readUtf(buf, 16));
            try {
                ImmutableMultimap<String, Property> properties = readProperties(buf);
                if (propertiesMethod == null) {
                    propertiesMethod = GameProfile.class.getMethod("getProperties");
                }
                PropertyMap propertiesMap = (PropertyMap) propertiesMethod.invoke(profile);
                propertiesMap.putAll(properties);
            } catch (final IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException e) {
                throw new IllegalStateException("Failed to set properties on GameProfile", e);
            }
        }
        return profile;
    }

    private static ImmutableMultimap<String, Property> readProperties(final ByteBuf buf) {
        final ImmutableMultimap.Builder<String, Property> propertiesBuilder =
                ImmutableMultimap.builder();
        final int properties = readVarInt(buf);
        for (int i1 = 0; i1 < properties; i1++) {
            final String name = readUtf(buf);
            final String value = readUtf(buf);
            final String signature = buf.readBoolean() ? readUtf(buf) : null;
            propertiesBuilder.put(name, new Property(name, value, signature));
        }
        return propertiesBuilder.build();
    }
}
