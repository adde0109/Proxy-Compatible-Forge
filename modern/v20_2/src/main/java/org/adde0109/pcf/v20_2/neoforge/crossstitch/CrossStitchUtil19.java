package org.adde0109.pcf.v20_2.neoforge.crossstitch;

import static org.adde0109.pcf.crossstitch.CrossStitch.MOD_ARGUMENT_INDICATOR_V2;
import static org.adde0109.pcf.crossstitch.CrossStitch.commandArgumentResourceKey;
import static org.adde0109.pcf.crossstitch.CrossStitch.commandArgumentTypeId;
import static org.adde0109.pcf.crossstitch.CrossStitch.shouldWrapArgument;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;

import org.adde0109.pcf.PCF;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * Adapted from <a
 * href="https://github.com/VelocityPowered/CrossStitch/blob/ebdf1209e8bfae4d6f3a53b636f61ecb1705ce34/src/main/java/com/velocitypowered/crossstitch/mixin/command/CommandTreeSerializationMixin.java">CrossStitch</a>
 */
public final class CrossStitchUtil19 {
    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>>
            void writeNode$wrapInVelocityModArgument19(
                    final @NotNull ByteBuf buffer,
                    final @NotNull ArgumentTypeInfo<A, T> serializer,
                    final @NotNull Object properties,
                    final @NotNull CallbackInfo ci) {
        if (!PCF.instance().crossStitch().enabled()) {
            return;
        }
        final FriendlyByteBuf buf = FriendlyByteBuf.wrap(buffer);
        Optional<String> identifier = commandArgumentResourceKey(serializer);
        if (identifier.isEmpty()) {
            if (PCF.instance().debug().enabled()) {
                PCF.logger.debug("Not wrapping argument with unknown identifier.");
            }
            return;
        }

        if (!shouldWrapArgument(identifier.get())) {
            if (PCF.instance().debug().enabled()) {
                PCF.logger.debug(
                        "Not wrapping argument with identifier: "
                                + identifier.get()
                                + " and id "
                                + commandArgumentTypeId(serializer));
            }
            return;
        }

        // Not a standard Minecraft argument type - so we need to wrap it
        PCF.logger.debug(
                "Wrapping argument with identifier: "
                        + identifier.get()
                        + " and id "
                        + commandArgumentTypeId(serializer));
        serializeWrappedArgumentType19(buf, serializer, properties);
        ci.cancel();
    }

    @SuppressWarnings("unchecked")
    private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>>
            void serializeWrappedArgumentType19(
                    final @NotNull FriendlyByteBuf buf,
                    final @NotNull ArgumentTypeInfo<A, T> serializer,
                    final @NotNull Object properties) {
        buf.writeVarInt(MOD_ARGUMENT_INDICATOR_V2);
        buf.writeVarInt(commandArgumentTypeId(serializer));

        net.minecraft.network.FriendlyByteBuf extraData =
                new net.minecraft.network.FriendlyByteBuf(Unpooled.buffer());
        serializer.serializeToNetwork((T) properties, extraData);

        buf.writeVarInt(extraData.readableBytes());
        buf.writeBytes(extraData);

        extraData.release();
    }
}
