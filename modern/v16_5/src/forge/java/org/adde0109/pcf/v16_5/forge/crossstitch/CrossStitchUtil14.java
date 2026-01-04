package org.adde0109.pcf.v16_5.forge.crossstitch;

import static org.adde0109.pcf.crossstitch.compat.CrossStitch.EMPTY_IDENTIFIER;
import static org.adde0109.pcf.crossstitch.compat.CrossStitch.MOD_ARGUMENT_INDICATOR;
import static org.adde0109.pcf.crossstitch.compat.CrossStitch.ZERO_LENGTH;
import static org.adde0109.pcf.crossstitch.compat.CrossStitch.shouldWrapArgument;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentTypes;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.mixin.v16_5.forge.crossstitch.ArgumentTypesAccessor;
import org.jetbrains.annotations.NotNull;

/**
 * Adapted from <a
 * href="https://github.com/VelocityPowered/CrossStitch/blob/fe3f3be49c52dc9c1b6b7cd3cafefb953adf4486/src/main/java/com/velocitypowered/crossstitch/mixin/command/CommandTreeSerializationMixin.java">CrossStitch</a>
 */
public final class CrossStitchUtil14 {
    @SuppressWarnings("unchecked")
    public static void writeNode$wrapInVelocityModArgument14(
            final @NotNull ByteBuf buffer, final @NotNull ArgumentType<?> argumentType) {
        if (!PCF.instance().crossStitch().enabled()) {
            return;
        }
        final FriendlyByteBuf buf = FriendlyByteBuf.wrap(buffer);
        final ArgumentTypes.Entry<?> entry = ArgumentTypesAccessor.get(argumentType);

        if (entry == null) {
            PCF.logger.debug(
                    "Wrapping entryless argument type: " + argumentType.getClass().getName());
            buf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);
            buf.writeResourceLocation(EMPTY_IDENTIFIER);
            buf.writeVarInt(ZERO_LENGTH);
            return;
        }

        final Object identifier = entry.name;
        if (!shouldWrapArgument(identifier.toString())) {
            buf.writeResourceLocation(identifier);
            ((ArgumentSerializer<ArgumentType<?>>) entry.serializer)
                    .serializeToNetwork(
                            argumentType, (net.minecraft.network.FriendlyByteBuf) buf.unwrap());
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
        serializeWrappedArgumentType(buf, argumentType, entry);
    }

    @SuppressWarnings("unchecked")
    private static void serializeWrappedArgumentType(
            final @NotNull FriendlyByteBuf buf,
            final @NotNull ArgumentType<?> argumentType,
            final @NotNull ArgumentTypes.Entry<?> entry) {
        buf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);
        buf.writeResourceLocation(entry.name);

        final net.minecraft.network.FriendlyByteBuf extraData =
                new net.minecraft.network.FriendlyByteBuf(Unpooled.buffer());
        ((ArgumentSerializer<ArgumentType<?>>) entry.serializer)
                .serializeToNetwork(argumentType, extraData);

        buf.writeVarInt(extraData.readableBytes());
        buf.writeBytes(extraData);

        extraData.release();
    }
}
