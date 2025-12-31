package org.adde0109.pcf.forwarding.network;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Utils copied (in part, as needed) from Minecraft's VarInt implementation. <br>
 * Given that, any who use this class must comply with Minecraft's EULA. This class exists purely
 * for compatibility's sake when dealing with multi-version code.
 */
final class VarInt {
    public static final int MAX_VARINT_SIZE = 5;
    private static final int DATA_BITS_MASK = 127;
    private static final int CONTINUATION_BIT_MASK = 128;
    private static final int DATA_BITS_PER_BYTE = 7;

    public static int getByteSize(final int bytes) {
        for (int i = 1; i < MAX_VARINT_SIZE; ++i) {
            if ((bytes & -1 << i * DATA_BITS_PER_BYTE) == 0) {
                return i;
            }
        }
        return MAX_VARINT_SIZE;
    }

    public static boolean hasContinuationBit(final byte b) {
        return (b & CONTINUATION_BIT_MASK) == CONTINUATION_BIT_MASK;
    }

    public static int read(final @NotNull ByteBuf buf) {
        int i = 0;
        int j = 0;

        byte b0;
        do {
            b0 = buf.readByte();
            i |= (b0 & DATA_BITS_MASK) << j++ * DATA_BITS_PER_BYTE;
            if (j > MAX_VARINT_SIZE) {
                throw new RuntimeException("VarInt too big");
            }
        } while (hasContinuationBit(b0));

        return i;
    }

    public static @NotNull ByteBuf write(final @NotNull ByteBuf buf, int varInt) {
        while ((varInt & -CONTINUATION_BIT_MASK) != 0) {
            buf.writeByte(varInt & DATA_BITS_MASK | CONTINUATION_BIT_MASK);
            varInt >>>= DATA_BITS_PER_BYTE;
        }
        buf.writeByte(varInt);
        return buf;
    }
}
