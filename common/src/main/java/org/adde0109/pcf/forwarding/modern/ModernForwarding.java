package org.adde0109.pcf.forwarding.modern;

import static org.adde0109.pcf.common.ByteBufUtils.readVarInt;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MAX_SUPPORTED_FORWARDING_VERSION;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.checkIntegrity;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.readAddress;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.abstractions.Connection;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

public final class ModernForwarding {
    public static Optional<String> forward(ByteBuf buf, Connection connection) {
        if (!checkIntegrity(buf)) {
            return Optional.of("Unable to verify player details");
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
        final int port = ((InetSocketAddress) connection.remoteAddress()).getPort();
        connection.setAddress(new InetSocketAddress(ip, port));

        return Optional.empty();
    }
}
