package org.adde0109.pcf.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.DecoderException;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Utils copied from Minecraft's FriendlyByteBuf, Utf8String, and VarInt implementations */
public final class ByteBufUtils {
    public static String readUtf(ByteBuf buf) {
        return readUtf(buf, Short.MAX_VALUE);
    }

    public static String readUtf(ByteBuf buf, int length) {
        return Utf8String.read(buf, length);
    }

    public static int readVarInt(ByteBuf buf) {
        return VarInt.read(buf);
    }

    public static UUID readUUID(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    private static final class Utf8String {
        public static String read(ByteBuf buf, int length) {
            int i = ByteBufUtil.utf8MaxBytes(length);
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
                    if (s.length() > length) {
                        int length1 = s.length();
                        throw new DecoderException(
                                "The received string length is longer than maximum allowed ("
                                        + length1
                                        + " > "
                                        + length
                                        + ")");
                    } else {
                        return s;
                    }
                }
            }
        }
    }

    private static final class VarInt {
        public static final int MAX_VARINT_SIZE = 5;
        private static final int DATA_BITS_MASK = 127;
        private static final int CONTINUATION_BIT_MASK = 128;
        private static final int DATA_BITS_PER_BYTE = 7;

        public static int getByteSize(int bytes) {
            for (int i = 1; i < 5; ++i) {
                if ((bytes & -1 << i * 7) == 0) {
                    return i;
                }
            }
            return 5;
        }

        public static boolean hasContinuationBit(byte b) {
            return (b & 128) == 128;
        }

        public static int read(ByteBuf buf) {
            int i = 0;
            int j = 0;

            byte b0;
            do {
                b0 = buf.readByte();
                i |= (b0 & 127) << j++ * 7;
                if (j > 5) {
                    throw new RuntimeException("VarInt too big");
                }
            } while (hasContinuationBit(b0));

            return i;
        }

        public static ByteBuf write(ByteBuf buf, int varInt) {
            while ((varInt & -128) != 0) {
                buf.writeByte(varInt & 127 | 128);
                varInt >>>= 7;
            }
            buf.writeByte(varInt);
            return buf;
        }
    }
}
