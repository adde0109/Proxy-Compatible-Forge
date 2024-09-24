//Contains code from: https://github.com/OKTW-Network/FabricProxy-Lite/blob/master/src/main/java/one/oktw/VelocityLib.java
package org.adde0109.pcf;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import org.adde0109.pcf.login.IMixinConnection;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ModernForwarding {

    private static final int SUPPORTED_FORWARDING_VERSION = 1;

    private final String forwardingSecret;

    ModernForwarding(String forwardingSecret) {
        this.forwardingSecret = forwardingSecret;
    }


    @Nullable
    public GameProfile handleForwardingPacket(ServerboundCustomQueryPacket packet, Connection connection) throws Exception {
        FriendlyByteBuf data = packet.getInternalData();
        if(data == null) {
            throw new Exception("Got empty packet");
        }

        if(!validate(data)) {
            throw new Exception("Player-data could not be validated!");
        }
        LogManager.getLogger().debug("Player-data validated!");

        int version = data.readVarInt();
        if (version != SUPPORTED_FORWARDING_VERSION) {
            throw new IllegalStateException("Unsupported forwarding version " + version + ", wanted " + SUPPORTED_FORWARDING_VERSION);
        }

        String ip = data.readUtf(Short.MAX_VALUE);
        SocketAddress address = connection.getRemoteAddress();
        int port = 0;
        if (address instanceof InetSocketAddress) {
            port = ((InetSocketAddress) address).getPort();
        }

        ((IMixinConnection) connection).pcf$setAddress(new InetSocketAddress(ip, port));

        GameProfile profile = new GameProfile(data.readUUID(), data.readUtf(16));
        readProperties(data, profile);
        return profile;
    }

    public boolean validate(FriendlyByteBuf buffer) {
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

    public void readProperties(FriendlyByteBuf buf, GameProfile profile) {
        PropertyMap properties = profile.getProperties();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            String name = buf.readUtf(Short.MAX_VALUE);
            String value = buf.readUtf(Short.MAX_VALUE);
            String signature = "";
            boolean hasSignature = buf.readBoolean();
            if (hasSignature) {
                signature = buf.readUtf(Short.MAX_VALUE);
            }
            properties.put(name, new Property(name, value, signature));
        }
    }
}
