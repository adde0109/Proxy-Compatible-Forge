package org.adde0109.pcf.forwarding.modern;

import static org.adde0109.pcf.common.Component.literal;
import static org.adde0109.pcf.common.Component.translatable;
import static org.adde0109.pcf.common.FByteBuf.readAddress;
import static org.adde0109.pcf.common.FByteBuf.readVarInt;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MODERN_MAX_VERSION;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.checkIntegrity;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.createProfile;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.PCF;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Utility class for modern forwarding handling */
public final class ModernForwarding {
    public static final Set<Integer> QUERY_IDS = ConcurrentHashMap.newKeySet();

    private static final @NotNull Object direct_conn_err =
            literal("This server requires you to connect with Velocity.");
    private static final @NotNull Object failed_to_verify =
            translatable("multiplayer.disconnect.unverified_username");

    @SuppressWarnings("unchecked")
    public static <T> @NotNull T DIRECT_CONNECT_ERR() {
        return (T) direct_conn_err;
    }

    @SuppressWarnings("unchecked")
    public static <T> @NotNull T FAILED_TO_VERIFY() {
        return (T) failed_to_verify;
    }

    public static @NotNull Data forward(
            final @NotNull ByteBuf buf, final @NotNull SocketAddress remoteAddress) {
        try {
            if (!checkIntegrity(buf)) {
                return new Data("Unable to verify player details");
            }
        } catch (AssertionError e) {
            if (e.getCause() instanceof InvalidKeyException
                    && PCF.instance().forwarding().secret().isBlank()) {
                PCF.logger.error(
                        "Please configure the `forwarding.secret` setting in PCF's config file!");
            } else {
                PCF.logger.error("An error occurred while validating player details: ", e);
            }
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
        public Data(final @NotNull String disconnectMsg) {
            this(-1, disconnectMsg, null, null);
        }
    }
}
