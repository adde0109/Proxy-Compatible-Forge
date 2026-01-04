package org.adde0109.pcf.crossstitch;

import com.mojang.brigadier.arguments.ArgumentType;

import io.netty.buffer.ByteBuf;

import org.jetbrains.annotations.NotNull;

public interface EntryBridge {
    @NotNull String bridge$identifier();

    void bridge$serializeToNetwork(
            final @NotNull ArgumentType<?> argument, final @NotNull ByteBuf buffer);
}
