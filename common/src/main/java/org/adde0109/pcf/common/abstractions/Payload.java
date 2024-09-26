package org.adde0109.pcf.common.abstractions;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

public interface Payload {
    int readVarInt();

    boolean readBoolean();

    String readUtf(int maxLength);

    default String readUtf() {
        return this.readUtf(Short.MAX_VALUE);
    }

    UUID readUUID();

    void readBytes(byte[] bytes);

    ByteBuf readBytes(int bytes);

    int readableBytes();

    int readerIndex();

    void getBytes(int index, byte[] bytes);

    @Nullable
    Payload readNullable(Function<Payload, Payload> function);
}
