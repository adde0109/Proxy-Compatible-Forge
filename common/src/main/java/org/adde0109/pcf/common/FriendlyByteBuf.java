package org.adde0109.pcf.common;

import static org.adde0109.pcf.common.Identifier.identifier;

import com.google.common.net.InetAddresses;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;

import org.adde0109.pcf.forwarding.network.codec.StreamDecoder;
import org.adde0109.pcf.forwarding.network.codec.StreamEncoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Utils copied (in part, as needed) from Minecraft's FriendlyByteBuf, Crypt, Utf8String, and VarInt
 * implementations. <br>
 * Given that, any who use this class must comply with Minecraft's EULA. This class exists purely
 * for compatibility's sake when dealing with multi-version code.
 */
@SuppressWarnings("UnusedReturnValue")
public final class FriendlyByteBuf extends ByteBuf {
    public static final short MAX_STRING_LENGTH = Short.MAX_VALUE;
    public static final int MAX_PAYLOAD_SIZE = 1048576; // 20 bits
    // Serverbound custom payload packet max size is only 32767

    private final @NotNull ByteBuf source;

    private FriendlyByteBuf(final @NotNull ByteBuf buf) {
        this.source = buf;
    }

    public FriendlyByteBuf() {
        this.source = Unpooled.buffer();
    }

    public static FriendlyByteBuf wrap(final @NotNull ByteBuf buf) {
        return new FriendlyByteBuf(buf);
    }

    // ---------------- FByteBuf methods -----------------
    public @NotNull InetAddress readAddress() {
        return readAddress(this.source);
    }

    public @NotNull ByteBuf readPayload(int maxSize) {
        return readPayload(this.source, maxSize);
    }

    public @NotNull ByteBuf readPayload() {
        return readPayload(this.source);
    }

    public @Nullable ByteBuf readNullablePayload(int maxSize) {
        return readNullablePayload(this.source, maxSize);
    }

    public @Nullable ByteBuf readNullablePayload() {
        return readNullablePayload(this.source);
    }

    // ---------------- FByteBuf static methods -----------------
    public static @NotNull InetAddress readAddress(final @NotNull ByteBuf buf) {
        return InetAddresses.forString(readUtf(buf));
    }

    public static @NotNull ByteBuf readPayload(final @NotNull ByteBuf buf, int maxSize) {
        int i = buf.readableBytes();
        if (i >= 0 && i <= maxSize) {
            return buf.readBytes(i);
        } else {
            throw new IllegalArgumentException(
                    "Payload may not be larger than " + maxSize + " bytes");
        }
    }

    public static @NotNull ByteBuf readPayload(final @NotNull ByteBuf buf) {
        return readPayload(buf, MAX_PAYLOAD_SIZE);
    }

    public static @Nullable ByteBuf readNullablePayload(final @NotNull ByteBuf buf, int maxSize) {
        return readNullable(buf, (b) -> readPayload(b, maxSize));
    }

    public static @Nullable ByteBuf readNullablePayload(final @NotNull ByteBuf buf) {
        return readNullable(buf, FriendlyByteBuf::readPayload);
    }

    public static void writePayload(
            final @NotNull ByteBuf buf, final @NotNull ByteBuf payload, int maxSize) {
        if (payload.readableBytes() > maxSize) {
            throw new IllegalArgumentException(
                    "Payload may not be larger than " + maxSize + " bytes");
        }
        buf.writeBytes(payload.slice());
    }

    public static void writePayload(final @NotNull ByteBuf buf, final @NotNull ByteBuf payload) {
        writePayload(buf, payload, MAX_PAYLOAD_SIZE);
    }

    public static void writeNullablePayload(
            final @NotNull ByteBuf buf, final @Nullable ByteBuf payload, int maxSize) {
        writeNullable(
                buf,
                payload,
                (b, p) -> {
                    if (p != null) {
                        writePayload(b, p, maxSize);
                    }
                });
    }

    public static void writeNullablePayload(
            final @NotNull ByteBuf buf, final @Nullable ByteBuf payload) {
        writeNullablePayload(buf, payload, MAX_PAYLOAD_SIZE);
    }

    // ---------------- FriendlyByteBuf methods -----------------
    public @NotNull String readUtf() {
        return readUtf(this.source);
    }

    public @NotNull String readUtf(int maxLength) {
        return readUtf(this.source, maxLength);
    }

    public int readVarInt() {
        return readVarInt(this.source);
    }

    public @NotNull UUID readUUID() {
        return readUUID(this.source);
    }

    public @NotNull ByteBuf writeUtf(final @NotNull String string, int maxLength) {
        return writeUtf(this.source, string, maxLength);
    }

    public @NotNull ByteBuf writeUtf(final @NotNull String string) {
        return writeUtf(this.source, string, MAX_STRING_LENGTH);
    }

    public @NotNull ByteBuf writeVarInt(final int input) {
        return writeVarInt(this.source, input);
    }

    public byte[] readByteArray(final int maxLength) {
        return readByteArray(this.source, maxLength);
    }

    public <T> @NotNull T readResourceLocation() {
        return readResourceLocation(this.source);
    }

    public @NotNull ByteBuf writeResourceLocation(final @NotNull Object resourceLocationIn) {
        return writeResourceLocation(this.source, resourceLocationIn);
    }

    public <T> Optional<T> readOptional(final @NotNull StreamDecoder<? super ByteBuf, T> decoder) {
        return readOptional(this.source, decoder);
    }

    public <T> void writeOptional(
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
                    final @NotNull Optional<T> optional,
            final @NotNull StreamEncoder<? super ByteBuf, T> encoder) {
        writeOptional(this.source, optional, encoder);
    }

    public @Nullable <T> T readNullable(final @NotNull StreamDecoder<? super ByteBuf, T> decoder) {
        return readNullable(this.source, decoder);
    }

    public <T> void writeNullable(
            final @Nullable T nullable, final @NotNull StreamEncoder<? super ByteBuf, T> encoder) {
        writeNullable(this.source, nullable, encoder);
    }

    public @NotNull Instant readInstant() {
        return readInstant(this.source);
    }

    public @NotNull PublicKey readPublicKey() {
        return readPublicKey(this.source);
    }

    // ---------------- FriendlyByteBuf static methods -----------------
    public static @NotNull String readUtf(final @NotNull ByteBuf buf) {
        return readUtf(buf, MAX_STRING_LENGTH);
    }

    public static @NotNull String readUtf(final @NotNull ByteBuf buf, int maxLength) {
        return Utf8String.read(buf, maxLength);
    }

    public static int readVarInt(final @NotNull ByteBuf buf) {
        return VarInt.read(buf);
    }

    public static @NotNull UUID readUUID(final @NotNull ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static @NotNull ByteBuf writeUtf(
            final @NotNull ByteBuf buf, final @NotNull String string) {
        return writeUtf(buf, string, MAX_STRING_LENGTH);
    }

    public static @NotNull ByteBuf writeUtf(
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
            writeVarInt(buf, bytes.length);
            buf.writeBytes(bytes);
            return buf;
        }
    }

    public static @NotNull ByteBuf writeVarInt(final @NotNull ByteBuf buf, int input) {
        while ((input & -VarInt.CONTINUATION_BIT_MASK) != 0) {
            buf.writeByte(input & VarInt.DATA_BITS_MASK | VarInt.CONTINUATION_BIT_MASK);
            input >>>= VarInt.DATA_BITS_PER_BYTE;
        }

        buf.writeByte(input);
        return buf;
    }

    public static byte[] readByteArray(final @NotNull ByteBuf buf, int maxLength) {
        int i = readVarInt(buf);
        if (i > maxLength) {
            throw new DecoderException(
                    "ByteArray with size " + i + " is bigger than allowed " + maxLength);
        } else {
            byte[] abyte = new byte[i];
            buf.readBytes(abyte);
            return abyte;
        }
    }

    public static <T> @NotNull T readResourceLocation(final @NotNull ByteBuf buf) {
        return identifier(readUtf(buf));
    }

    public static @NotNull ByteBuf writeResourceLocation(
            final @NotNull ByteBuf buf, final @NotNull Object resourceLocationIn) {
        writeUtf(buf, resourceLocationIn.toString(), MAX_STRING_LENGTH);
        return buf;
    }

    public static <T> Optional<T> readOptional(
            final @NotNull ByteBuf buf, final @NotNull StreamDecoder<? super ByteBuf, T> decoder) {
        return buf.readBoolean() ? Optional.of(decoder.decode(buf)) : Optional.empty();
    }

    public static <T> void writeOptional(
            final ByteBuf buf,
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
                    final @NotNull Optional<T> optional,
            final @NotNull StreamEncoder<? super ByteBuf, T> encoder) {
        if (optional.isPresent()) {
            buf.writeBoolean(true);
            encoder.encode(buf, optional.get());
        } else {
            buf.writeBoolean(false);
        }
    }

    public static @Nullable <T> T readNullable(
            final @NotNull ByteBuf buf, final @NotNull StreamDecoder<? super ByteBuf, T> decoder) {
        return buf.readBoolean() ? decoder.decode(buf) : null;
    }

    public static <T> void writeNullable(
            final @NotNull ByteBuf buf,
            final @Nullable T nullable,
            final @NotNull StreamEncoder<? super ByteBuf, T> encoder) {
        if (nullable != null) {
            buf.writeBoolean(true);
            encoder.encode(buf, nullable);
        } else {
            buf.writeBoolean(false);
        }
    }

    public static @NotNull Instant readInstant(final @NotNull ByteBuf buf) {
        return Instant.ofEpochMilli(buf.readLong());
    }

    public static @NotNull PublicKey readPublicKey(final @NotNull ByteBuf buf)
            throws DecoderException {
        try {
            return Crypt.byteToPublicKey(readByteArray(buf, Crypt.MAX_PUBLIC_KEY_LENGTH));
        } catch (CryptException e) {
            throw new DecoderException("Malformed public key bytes", e);
        }
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

    // Note: Added in Netty 4.1, not available below 1.12
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

    public static final class Crypt {
        public static final String ASYMMETRIC_ALGORITHM = "RSA";
        public static final int MAX_KEY_SIGNATURE_SIZE = 4096;
        public static final int MAX_PUBLIC_KEY_LENGTH = 512;

        public static PublicKey byteToPublicKey(byte[] bytes) throws CryptException {
            try {
                EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(bytes);
                KeyFactory keyfactory = KeyFactory.getInstance(ASYMMETRIC_ALGORITHM);
                return keyfactory.generatePublic(encodedkeyspec);
            } catch (Exception exception) {
                throw new CryptException(exception);
            }
        }
    }

    public static final class CryptException extends Exception {
        public CryptException(final @NotNull Throwable cause) {
            super(cause);
        }
    }

    private static final class Utf8String {
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
    }

    private static final class ByteBufUtil {
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

    private static final class VarInt {
        public static final int MAX_VARINT_SIZE = 5;
        private static final int DATA_BITS_MASK = 127;
        private static final int CONTINUATION_BIT_MASK = 128;
        private static final int DATA_BITS_PER_BYTE = 7;

        public static int getByteSize(int bytes) {
            for (int i = 1; i < MAX_VARINT_SIZE; ++i) {
                if ((bytes & -1 << i * DATA_BITS_PER_BYTE) == 0) {
                    return i;
                }
            }
            return MAX_VARINT_SIZE;
        }

        public static boolean hasContinuationBit(byte b) {
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
}
