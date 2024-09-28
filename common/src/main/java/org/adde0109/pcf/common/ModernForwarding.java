// Contains code from:
// https://github.com/OKTW-Network/FabricProxy-Lite/blob/master/src/main/java/one/oktw/VelocityLib.java
package org.adde0109.pcf.common;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import org.adde0109.pcf.common.abstractions.Connection;
import org.adde0109.pcf.common.abstractions.Payload;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ModernForwarding {
    private static final int SUPPORTED_FORWARDING_VERSION = 1;
    private final String forwardingSecret;

    public ModernForwarding(String forwardingSecret) {
        this.forwardingSecret = forwardingSecret;
    }

    @Nullable public GameProfile handleForwardingPacket(Payload data, Connection conn) throws Exception {
        if (!this.validate(data)) {
            throw new Exception("Player-data could not be validated!");
        }
        LogManager.getLogger().debug("Player-data validated!");

        int version = data.readVarInt();
        if (version != SUPPORTED_FORWARDING_VERSION) {
            throw new IllegalStateException(
                    "Unsupported forwarding version "
                            + version
                            + ", wanted "
                            + SUPPORTED_FORWARDING_VERSION);
        }

        String ip = data.readUtf();
        SocketAddress address = conn.remoteAddress();
        int port = 0;
        if (address instanceof InetSocketAddress) {
            port = ((InetSocketAddress) address).getPort();
        }

        conn.setAddress(new InetSocketAddress(ip, port));

        GameProfile profile = new GameProfile(data.readUUID(), data.readUtf(16));
        this.readProperties(data, profile);
        return profile;
    }

    public boolean validate(Payload buffer) {
        final byte[] signature = new byte[32];
        buffer.readBytes(signature);

        final byte[] data = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), data);

        try {
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(forwardingSecret.getBytes(), "HmacSHA256"));
            final byte[] mySignature = mac.doFinal(data);
            if (!MessageDigest.isEqual(signature, mySignature)) {
                return false;
            }
        } catch (final InvalidKeyException | NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        return true;
    }

    public void readProperties(Payload buf, GameProfile profile) {
        PropertyMap properties = profile.getProperties();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            String name = buf.readUtf();
            String value = buf.readUtf();
            String signature = "";
            boolean hasSignature = buf.readBoolean();
            if (hasSignature) {
                signature = buf.readUtf();
            }
            properties.put(name, new Property(name, value, signature));
        }
    }
}
