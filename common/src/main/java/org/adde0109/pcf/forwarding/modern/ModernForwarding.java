package org.adde0109.pcf.forwarding.modern;

import static org.adde0109.pcf.common.FByteBuf.readVarInt;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MAX_SUPPORTED_FORWARDING_VERSION;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.checkIntegrity;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.createProfile;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.readAddress;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.PCF;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Utility class for modern forwarding handling */
public final class ModernForwarding {
    public static final Set<Integer> QUERY_IDS = ConcurrentHashMap.newKeySet();

    public static Data forward(ByteBuf buf, InetSocketAddress remoteAddress) {
        if (!checkIntegrity(buf)) {
            return new Data("Unable to verify player details");
        }
        PCF.logger.debug("Player-data validated!");

        int version = readVarInt(buf);
        if (version > MAX_SUPPORTED_FORWARDING_VERSION) {
            throw new IllegalStateException(
                    "Unsupported forwarding version "
                            + version
                            + ", wanted up to "
                            + MAX_SUPPORTED_FORWARDING_VERSION);
        }

        final InetAddress ip = readAddress(buf);
        final int port = remoteAddress.getPort();
        final InetSocketAddress address = new InetSocketAddress(ip, port);
        final GameProfile profile = createProfile(buf);

        return new Data(null, address, profile);
    }

    public record Data(
            @Nullable String disconnectMsg,
            @Nullable SocketAddress address,
            @Nullable GameProfile profile) {
        public Data(String disconnectMsg) {
            this(disconnectMsg, null, null);
        }
    }
}
