package org.adde0109.pcf.forwarding.network;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.CharsetUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

/**
 * Utils copied (in part, as needed) from Minecraft's Utf8String implementation. <br>
 * Given that, any who use this class must comply with Minecraft's EULA. This class exists purely
 * for compatibility's sake when dealing with multi-version code.
 */
final class Utf8String {
    public static @NotNull String read(final @NotNull ByteBuf buf, int maxLength) {
        int i = ByteBufUtil.utf8MaxBytes(maxLength);
        int j = VarInt.read(buf);
        if (j > i) {
            throw new DecoderException(
                    "The received encoded string buffer length is longer than maximum allowed ("
                            + j
                            + " > "
                            + i
                            + ")");
        } else if (j < 0) {
            throw new DecoderException(
                    "The received encoded string buffer length is less than zero! Weird string!");
        } else {
            int k = buf.readableBytes();
            if (j > k) {
                throw new DecoderException(
                        "Not enough bytes in buffer, expected " + j + ", but got " + k);
            } else {
                String s = buf.toString(buf.readerIndex(), j, StandardCharsets.UTF_8);
                buf.readerIndex(buf.readerIndex() + j);
                if (s.length() > maxLength) {
                    int length = s.length();
                    throw new DecoderException(
                            "The received string length is longer than maximum allowed ("
                                    + length
                                    + " > "
                                    + maxLength
                                    + ")");
                } else {
                    return s;
                }
            }
        }
    }

    public static @NotNull ByteBuf write(
            final @NotNull ByteBuf buf, final @NotNull String string, int maxLength) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > maxLength) {
            throw new EncoderException(
                    "String too big (was "
                            + bytes.length
                            + " bytes encoded, max "
                            + maxLength
                            + ")");
        } else {
            VarInt.write(buf, bytes.length);
            buf.writeBytes(bytes);
            return buf;
        }
    }

    static final class ByteBufUtil {
        private static final int MAX_BYTES_PER_CHAR_UTF8;

        static { // Netty 4.1 differences
            if (MetaAPI.instance().version().isAtLeast(MinecraftVersions.V12)) {
                MAX_BYTES_PER_CHAR_UTF8 =
                        (int) CharsetUtil.encoder(CharsetUtil.UTF_8).maxBytesPerChar();
            } else {
                //noinspection deprecation
                MAX_BYTES_PER_CHAR_UTF8 =
                        (int) CharsetUtil.getEncoder(CharsetUtil.UTF_8).maxBytesPerChar();
            }
        }

        public static int utf8MaxBytes(final int seqLength) {
            return seqLength * MAX_BYTES_PER_CHAR_UTF8;
        }
    }
}
