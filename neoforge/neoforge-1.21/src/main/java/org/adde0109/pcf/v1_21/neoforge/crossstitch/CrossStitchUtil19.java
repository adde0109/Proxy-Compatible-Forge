package org.adde0109.pcf.v1_21.neoforge.crossstitch;

import static org.adde0109.pcf.v1_20_2.neoforge.crossstitch.CSBootstrap.commandArgumentResourceKey;
import static org.adde0109.pcf.v1_20_2.neoforge.crossstitch.CSBootstrap.commandArgumentTypeId;

import com.mojang.brigadier.arguments.ArgumentType;

import io.netty.buffer.Unpooled;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_20_2.neoforge.crossstitch.CSBootstrap;
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
        Optional<ResourceLocation> identifier =
                commandArgumentResourceKey(serializer)
                        .map(ResourceKey::location)
                        .filter(CSBootstrap::shouldWrapArgument);
        if (identifier.isEmpty()) {
            return;
        }

        // Not a standard Minecraft argument type - so we need to wrap it
        PCF.logger.debug("Wrapping argument with identifier: " + identifier.get());
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

        FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
        serializer.serializeToNetwork((T) properties, extraData);

        buf.writeVarInt(extraData.readableBytes());
        buf.writeBytes(extraData);

        extraData.release();
    }
}
