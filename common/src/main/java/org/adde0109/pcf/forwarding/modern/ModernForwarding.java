package org.adde0109.pcf.forwarding.modern;

import static org.adde0109.pcf.common.FByteBuf.readAddress;
import static org.adde0109.pcf.common.FByteBuf.readVarInt;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_MAX_VERSION;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.checkIntegrity;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.createProfile;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.PCF;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Utility class for modern forwarding handling */
public final class ModernForwarding {
    public static final Set<Integer> QUERY_IDS = ConcurrentHashMap.newKeySet();

    public static Data forward(ByteBuf buf, SocketAddress remoteAddress) {
        if (!checkIntegrity(buf)) {
            return new Data("Unable to verify player details");
        }
        PCF.logger.debug("Player-data validated!");

        int version = readVarInt(buf);
        if (version > MODERN_MAX_VERSION) {
            throw new IllegalStateException(
                    "Unsupported forwarding version "
                            + version
                            + ", wanted up to "
                            + MODERN_MAX_VERSION);
        }
        PCF.logger.debug("Using modern forwarding version: " + version);

        final InetSocketAddress address =
                new InetSocketAddress(
                        readAddress(buf), ((InetSocketAddress) remoteAddress).getPort());
        final GameProfile profile = createProfile(buf);

        return new Data(version, null, address, profile);
    }

    public record Data(
            int version,
            @Nullable String disconnectMsg,
            @Nullable SocketAddress address,
            @Nullable GameProfile profile) {
        public Data(String disconnectMsg) {
            this(-1, disconnectMsg, null, null);
        }
    }
}
