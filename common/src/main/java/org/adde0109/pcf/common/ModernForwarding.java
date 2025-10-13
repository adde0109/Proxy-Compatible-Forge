package org.adde0109.pcf.common;

import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.abstractions.Connection;
import org.adde0109.pcf.common.abstractions.Payload;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Adapted from <a
 * href="https://github.com/OKTW-Network/FabricProxy-Lite/blob/master/src/main/java/one/oktw/VelocityLib.java">FabricProxy-Lite</a>
 */
public final class ModernForwarding {
    private ModernForwarding() {}

    public static final int QUERY_ID = 1203961429;
    private static final int SUPPORTED_FORWARDING_VERSION = 1;

    private static final boolean isAtLeast21_9 =
            MetaAPI.instance().version().isAtLeast(MinecraftVersions.V21_9);
    private static Method propertiesMethod;

    public static boolean validate(final Payload buffer) {
        final byte[] signature = new byte[32];
        buffer.readBytes(signature);

        final byte[] data = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), data);

        try {
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(PCF.instance().forwarding().secret().getBytes(), "HmacSHA256"));
            final byte[] mySignature = mac.doFinal(data);
            if (!MessageDigest.isEqual(signature, mySignature)) {
                return false;
            }
        } catch (final InvalidKeyException | NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        return true;
    }

    public static @NotNull GameProfile handleForwardingPacket(Payload data, Connection conn)
            throws Exception {
        if (!validate(data)) {
            throw new Exception("Player-data could not be validated!");
        }
        PCF.logger.debug("Player-data validated!");

        final int version = data.readVarInt();
        if (version != SUPPORTED_FORWARDING_VERSION) {
            throw new IllegalStateException(
                    "Unsupported forwarding version "
                            + version
                            + ", wanted "
                            + SUPPORTED_FORWARDING_VERSION);
        }

        final String ip = data.readUtf();
        conn.setAddress(
                new InetSocketAddress(ip, ((InetSocketAddress) conn.remoteAddress()).getPort()));

        final GameProfile profile;
        if (isAtLeast21_9) { // com.mojang:authlib:7.0.0 or newer
            profile =
                    new GameProfile(
                            data.readUUID(),
                            data.readUtf(16),
                            new PropertyMap(readProperties(data)));
        } else {
            profile = new GameProfile(data.readUUID(), data.readUtf(16));
            ImmutableMultimap<String, Property> properties = readProperties(data);
            if (propertiesMethod == null) {
                propertiesMethod = GameProfile.class.getMethod("getProperties");
            }
            PropertyMap propertiesMap = (PropertyMap) propertiesMethod.invoke(profile);
            propertiesMap.putAll(properties);
        }
        return profile;
    }

    public static ImmutableMultimap<String, Property> readProperties(Payload buf) {
        final ImmutableMultimap.Builder<String, Property> propertiesBuilder =
                ImmutableMultimap.builder();
        final int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            String name = buf.readUtf();
            String value = buf.readUtf();
            String signature = "";
            boolean hasSignature = buf.readBoolean();
            if (hasSignature) {
                signature = buf.readUtf();
            }
            propertiesBuilder.put(name, new Property(name, value, signature));
        }
        return propertiesBuilder.build();
    }
}
