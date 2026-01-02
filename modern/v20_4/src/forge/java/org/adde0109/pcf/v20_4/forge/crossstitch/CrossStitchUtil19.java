package org.adde0109.pcf.v20_4.forge.crossstitch;

import static org.adde0109.pcf.v20_4.forge.crossstitch.CSBootstrap.commandArgumentResourceKey;
import static org.adde0109.pcf.v20_4.forge.crossstitch.CSBootstrap.commandArgumentTypeId;
import static org.adde0109.pcf.v20_4.forge.crossstitch.CSBootstrap.shouldWrapArgument;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;

import io.netty.buffer.Unpooled;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCF;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * Adapted from <a
 * href="https://github.com/VelocityPowered/CrossStitch/blob/ebdf1209e8bfae4d6f3a53b636f61ecb1705ce34/src/main/java/com/velocitypowered/crossstitch/mixin/command/CommandTreeSerializationMixin.java">CrossStitch</a>
 */
public final class CrossStitchUtil19 {
    private static final int MOD_ARGUMENT_INDICATOR = -256;

    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>>
            void writeNode$wrapInVelocityModArgument19(
                    FriendlyByteBuf buf,
                    ArgumentTypeInfo<A, T> serializer,
                    ArgumentTypeInfo.Template<A> properties,
                    CallbackInfo ci) {
        if (!PCF.instance().crossStitch().enabled()) {
            return;
        }
        Optional<ResourceLocation> identifier =
                commandArgumentResourceKey(serializer).map(ResourceKey::location);
        if (identifier.isEmpty()) {
            if (PCF.instance().debug().enabled()) {
                PCF.logger.debug("Not wrapping argument with unknown identifier.");
            }
            return;
        } else if (!shouldWrapArgument(identifier.get())) {
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
        // minecraft:command_argument_type might need wrapping on some Forge versions?
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
                    FriendlyByteBuf buf,
                    ArgumentTypeInfo<A, T> serializer,
                    ArgumentTypeInfo.Template<A> properties) {
        buf.writeVarInt(MOD_ARGUMENT_INDICATOR);
        buf.writeVarInt(commandArgumentTypeId(serializer));

        net.minecraft.network.FriendlyByteBuf extraData =
                new net.minecraft.network.FriendlyByteBuf(Unpooled.buffer());
        serializer.serializeToNetwork((T) properties, extraData);

        buf.writeVarInt(extraData.readableBytes());
        buf.writeBytes(extraData);

        extraData.release();
    }
}
