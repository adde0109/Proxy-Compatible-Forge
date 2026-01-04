package org.adde0109.pcf.crossstitch;

import static dev.neuralnexus.taterapi.resources.Identifier.identifier;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.crossstitch.compat.ArgumentEdgeCases;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public final class CrossStitch {
    public static final Object MOD_ARGUMENT_INDICATOR = identifier("crossstitch:mod_argument");
    public static final int MOD_ARGUMENT_INDICATOR_V2 = -256;
    public static final Object EMPTY_IDENTIFIER = identifier("");
    public static final int ZERO_LENGTH = 0;

    private static final Set<String> BUILT_IN_REGISTRY_KEYS = Set.of("minecraft", "brigadier");

    public static boolean shouldWrapArgument(final @NotNull String identifier) {
        final boolean isVanilla = BUILT_IN_REGISTRY_KEYS.stream().anyMatch(identifier::startsWith);
        final boolean forceWrapped =
                PCF.instance().crossStitch().forceWrappedArguments().stream()
                        .anyMatch(identifier::equals);
        final boolean forceWrapVanilla =
                PCF.instance().crossStitch().forceWrapVanillaArguments() && isVanilla;
        final boolean isEdgeCase = ArgumentEdgeCases.isArgumentEdgeCase(identifier);
        return forceWrapped || forceWrapVanilla || isEdgeCase || !isVanilla;
    }

    public static Function<@NotNull ArgumentType<?>, @Nullable Object> GET_ARGUMENT_TYPE_ENTRY;

    private static @Nullable EntryBridge getArgumentTypeEntry(
            final @NotNull ArgumentType<?> argumentType) {
        return (EntryBridge) GET_ARGUMENT_TYPE_ENTRY.apply(argumentType);
    }

    /**
     * Adapted from <a
     * href="https://github.com/VelocityPowered/CrossStitch/blob/fe3f3be49c52dc9c1b6b7cd3cafefb953adf4486/src/main/java/com/velocitypowered/crossstitch/mixin/command/CommandTreeSerializationMixin.java">CrossStitch</a>
     */
    public static void writeNode$wrapInVelocityModArgument(
            final @NotNull ByteBuf buffer, final @NotNull ArgumentType<?> argumentType) {
        if (!PCF.instance().crossStitch().enabled()) {
            return;
        }
        final FriendlyByteBuf buf = FriendlyByteBuf.wrap(buffer);
        final EntryBridge entry = getArgumentTypeEntry(argumentType);

        if (entry == null) {
            PCF.logger.debug(
                    "Wrapping entryless argument type: " + argumentType.getClass().getName());
            buf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);
            buf.writeResourceLocation(EMPTY_IDENTIFIER);
            buf.writeVarInt(ZERO_LENGTH);
            return;
        }

        final String identifier = entry.bridge$identifier();
        if (!shouldWrapArgument(identifier)) {
            buf.writeResourceLocation(identifier);
            entry.bridge$serializeToNetwork(argumentType, buf);
            if (PCF.instance().debug().enabled()) {
                PCF.logger.debug("Not wrapping argument with identifier: " + identifier);
            }
            return;
        }

        // Not a standard Minecraft argument type - so we need to wrap it
        PCF.logger.debug("Wrapping argument with identifier: " + identifier);
        if (PCF.instance().debug().enabled()) {
            PCF.logger.debug("Wrapping argument with type: " + argumentType.getClass().getName());
        }

        // Serialize wrapped argument type
        buf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);
        buf.writeResourceLocation(entry.bridge$identifier());

        final ByteBuf extraData = Unpooled.buffer();
        entry.bridge$serializeToNetwork(argumentType, extraData);

        buf.writeVarInt(extraData.readableBytes());
        buf.writeBytes(extraData);

        extraData.release();
    }

    public static Function<@NotNull Object, Optional<String>> COMMAND_ARGUMENT_IDENTIFIER;

    public static Optional<String> commandArgumentResourceKey(final @NotNull Object type) {
        return COMMAND_ARGUMENT_IDENTIFIER.apply(type);
    }

    public static Function<@NotNull Object, Integer> COMMAND_ARGUMENT_TYPE_ID;

    public static int commandArgumentTypeId(final @NotNull Object type) {
        return COMMAND_ARGUMENT_TYPE_ID.apply(type);
    }
}
