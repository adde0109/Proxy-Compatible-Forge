package org.adde0109.pcf.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Function;

/** Utils copied from Minecraft's FriendlyByteBuf, Utf8String, and VarInt implementations */
@SuppressWarnings("UnusedReturnValue")
public final class FByteBuf extends ByteBuf {
    private final ByteBuf source;

    private FByteBuf(ByteBuf buf) {
        this.source = buf;
    }

    public FByteBuf() {
        this.source = Unpooled.buffer();
    }

    public static FByteBuf wrap(ByteBuf buf) {
        return new FByteBuf(buf);
    }

    // ---------------- FriendlyByteBuf methods -----------------
    public String readUtf() {
        return readUtf(this.source);
    }

    public String readUtf(int maxLength) {
        return readUtf(this.source, maxLength);
    }

    public int readVarInt() {
        return readVarInt(this.source);
    }

    public UUID readUUID() {
        return readUUID(this.source);
    }

    public ByteBuf writeUtf(String string, int maxLength) {
        return writeUtf(this.source, string, maxLength);
    }

    public ByteBuf writeUtf(String string) {
        return writeUtf(this.source, string, Short.MAX_VALUE);
    }

    public ByteBuf writeVarInt(int input) {
        return writeVarInt(this.source, input);
    }

    public ByteBuf writeResourceLocation(Object resourceLocationIn) {
        return writeResourceLocation(this.source, resourceLocationIn);
    }

    public @Nullable <T> T readNullable(Function<ByteBuf, T> function) {
        return readNullable(this.source, function);
    }

    // ---------------- FriendlyByteBuf static methods -----------------

    public static String readUtf(ByteBuf buf) {
        return readUtf(buf, Short.MAX_VALUE);
    }

    public static String readUtf(ByteBuf buf, int maxLength) {
        return Utf8String.read(buf, maxLength);
    }

    public static int readVarInt(ByteBuf buf) {
        return VarInt.read(buf);
    }

    public static UUID readUUID(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static ByteBuf writeUtf(ByteBuf buf, String string, int maxLength) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > maxLength) {
            throw new EncoderException(
                    "String too big (was "
                            + bytes.length
                            + " bytes encoded, max "
                            + maxLength
                            + ")");
        } else {
            writeVarInt(buf, bytes.length);
            buf.writeBytes(bytes);
            return buf;
        }
    }

    public static ByteBuf writeVarInt(ByteBuf buf, int input) {
        while ((input & -128) != 0) {
            buf.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        buf.writeByte(input);
        return buf;
    }

    public static ByteBuf writeResourceLocation(ByteBuf buf, Object resourceLocationIn) {
        writeUtf(buf, resourceLocationIn.toString(), Short.MAX_VALUE);
        return buf;
    }

    public static @Nullable <T> T readNullable(ByteBuf buf, Function<ByteBuf, T> function) {
        return buf.readBoolean() ? function.apply(buf) : null;
    }

    // ---------------- ByteBuf methods -----------------

    @Override
    public int capacity() {
        return this.source.capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        return this.source.capacity(newCapacity);
    }

    @Override
    public int maxCapacity() {
        return this.source.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.source.alloc();
    }

    @Deprecated
    @Override
    public ByteOrder order() {
        return this.source.order();
    }

    @Deprecated
    @Override
    public ByteBuf order(ByteOrder endianness) {
        return this.source.order(endianness);
    }

    @Override
    public ByteBuf unwrap() {
        return this.source;
    }

    @Override
    public boolean isDirect() {
        return this.source.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return this.source.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        return this.source.asReadOnly();
    }

    @Override
    public int readerIndex() {
        return this.source.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int readerIndex) {
        return this.source.readerIndex(readerIndex);
    }

    @Override
    public int writerIndex() {
        return this.source.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int writerIndex) {
        return this.source.writerIndex(writerIndex);
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        return this.source.setIndex(readerIndex, writerIndex);
    }

    @Override
    public int readableBytes() {
        return this.source.readableBytes();
    }

    @Override
    public int writableBytes() {
        return this.source.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return this.source.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return this.source.isReadable();
    }

    @Override
    public boolean isReadable(int size) {
        return this.source.isReadable(size);
    }

    @Override
    public boolean isWritable() {
        return this.source.isWritable();
    }

    @Override
    public boolean isWritable(int size) {
        return this.source.isWritable(size);
    }

    @Override
    public ByteBuf clear() {
        return this.source.clear();
    }

    @Override
    public ByteBuf markReaderIndex() {
        return this.source.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return this.source.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return this.source.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return this.source.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes() {
        return this.source.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return this.source.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        return this.source.ensureWritable(minWritableBytes);
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return this.source.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        return this.source.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return this.source.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return this.source.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return this.source.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        return this.source.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return this.source.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return this.source.getUnsignedShortLE(index);
    }

    @Override
    public int getMedium(int index) {
        return this.source.getMedium(index);
    }

    @Override
    public int getMediumLE(int index) {
        return this.source.getMediumLE(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return this.source.getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return this.source.getUnsignedMediumLE(index);
    }

    @Override
    public int getInt(int index) {
        return this.source.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        return this.source.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return this.source.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return this.source.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(int index) {
        return this.source.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        return this.source.getLongLE(index);
    }

    @Override
    public char getChar(int index) {
        return this.source.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return this.source.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return this.source.getDouble(index);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        return this.source.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        return this.source.getBytes(index, dst, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        return this.source.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        return this.source.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        return this.source.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        return this.source.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        return this.source.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return this.source.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return this.source.getBytes(index, out, position, length);
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return this.source.getCharSequence(index, length, charset);
    }

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        return this.source.setBoolean(index, value);
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        return this.source.setByte(index, value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        return this.source.setShort(index, value);
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        return this.source.setShortLE(index, value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        return this.source.setMedium(index, value);
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        return this.source.setMediumLE(index, value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        return this.source.setInt(index, value);
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        return this.source.setIntLE(index, value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        return this.source.setLong(index, value);
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        return this.source.setLongLE(index, value);
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        return this.source.setChar(index, value);
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        return this.source.setFloat(index, value);
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        return this.source.setDouble(index, value);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        return this.source.setBytes(index, src);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        return this.source.setBytes(index, src, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        return this.source.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        return this.source.setBytes(index, src);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        return this.source.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        return this.source.setBytes(index, src);
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return this.source.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return this.source.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return this.source.setBytes(index, in, position, length);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        return this.source.setZero(index, length);
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return this.source.setCharSequence(index, sequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return this.source.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.source.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return this.source.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return this.source.readShort();
    }

    @Override
    public short readShortLE() {
        return this.source.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return this.source.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return this.source.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return this.source.readMedium();
    }

    @Override
    public int readMediumLE() {
        return this.source.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return this.source.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return this.source.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return this.source.readInt();
    }

    @Override
    public int readIntLE() {
        return this.source.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return this.source.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return this.source.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return this.source.readLong();
    }

    @Override
    public long readLongLE() {
        return this.source.readLongLE();
    }

    @Override
    public char readChar() {
        return this.source.readChar();
    }

    @Override
    public float readFloat() {
        return this.source.readFloat();
    }

    @Override
    public double readDouble() {
        return this.source.readDouble();
    }

    @Override
    public ByteBuf readBytes(int length) {
        return this.source.readBytes(length);
    }

    @Override
    public ByteBuf readSlice(int length) {
        return this.source.readSlice(length);
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return this.source.readRetainedSlice(length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        return this.source.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        return this.source.readBytes(dst, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        return this.source.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        return this.source.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        return this.source.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        return this.source.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        return this.source.readBytes(out, length);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return this.source.readBytes(out, length);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return this.source.readCharSequence(length, charset);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return this.source.readBytes(out, position, length);
    }

    @Override
    public ByteBuf skipBytes(int length) {
        return this.source.skipBytes(length);
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        return this.source.writeBoolean(value);
    }

    @Override
    public ByteBuf writeByte(int value) {
        return this.source.writeByte(value);
    }

    @Override
    public ByteBuf writeShort(int value) {
        return this.source.writeShort(value);
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        return this.source.writeShortLE(value);
    }

    @Override
    public ByteBuf writeMedium(int value) {
        return this.source.writeMedium(value);
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        return this.source.writeMediumLE(value);
    }

    @Override
    public ByteBuf writeInt(int value) {
        return this.source.writeInt(value);
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        return this.source.writeIntLE(value);
    }

    @Override
    public ByteBuf writeLong(long value) {
        return this.source.writeLong(value);
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        return this.source.writeLongLE(value);
    }

    @Override
    public ByteBuf writeChar(int value) {
        return this.source.writeChar(value);
    }

    @Override
    public ByteBuf writeFloat(float value) {
        return this.source.writeFloat(value);
    }

    @Override
    public ByteBuf writeDouble(double value) {
        return this.source.writeDouble(value);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        return this.source.writeBytes(src);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        return this.source.writeBytes(src, length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        return this.source.writeBytes(src, srcIndex, length);
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        return this.source.writeBytes(src);
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        return this.source.writeBytes(src, srcIndex, length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        return this.source.writeBytes(src);
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return this.source.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return this.source.writeBytes(in, length);
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return this.source.writeBytes(in, position, length);
    }

    @Override
    public ByteBuf writeZero(int length) {
        return this.source.writeZero(length);
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return this.source.writeCharSequence(sequence, charset);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return this.source.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return this.source.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return this.source.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return this.source.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        return this.source.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return this.source.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return this.source.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return this.source.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuf copy() {
        return this.source.copy();
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return this.source.copy(index, length);
    }

    @Override
    public ByteBuf slice() {
        return this.source.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.source.retainedSlice();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return this.source.slice(index, length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return this.source.retainedSlice(index, length);
    }

    @Override
    public ByteBuf duplicate() {
        return this.source.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.source.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return this.source.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.source.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return this.source.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return this.source.internalNioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.source.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return this.source.nioBuffers(index, length);
    }

    @Override
    public boolean hasArray() {
        return this.source.hasArray();
    }

    @Override
    public byte[] array() {
        return this.source.array();
    }

    @Override
    public int arrayOffset() {
        return this.source.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.source.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.source.memoryAddress();
    }

    @Override
    public String toString(Charset charset) {
        return this.source.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return this.source.toString(index, length, charset);
    }

    @Override
    public int hashCode() {
        return this.source.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ByteBuf)) {
            return false;
        }
        return this.source.equals(o);
    }

    @Override
    public int compareTo(ByteBuf byteBuffer) {
        return this.source.compareTo(byteBuffer);
    }

    @Override
    public String toString() {
        return this.source.toString();
    }

    @Override
    public ByteBuf retain(int increment) {
        return this.source.retain(increment);
    }

    @Override
    public int refCnt() {
        return this.source.refCnt();
    }

    @Override
    public ByteBuf retain() {
        return this.source.retain();
    }

    @Override
    public ByteBuf touch() {
        return this.source.touch();
    }

    @Override
    public ByteBuf touch(Object hint) {
        return this.source.touch(hint);
    }

    @Override
    public boolean release() {
        return this.source.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.source.release(decrement);
    }

    private static final class Utf8String {
        public static String read(ByteBuf buf, int maxLength) {
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

    // ---------------- ByteBuf methods -----------------
}
