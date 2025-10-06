package org.adde0109.pcf.v1_19.forge.crossstitch;

import com.mojang.brigadier.arguments.ArgumentType;

import io.netty.buffer.Unpooled;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCF;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        ResourceLocation identifier = (ResourceLocation) PCF.commandArgumentTypeKey(serializer);

        if (identifier == null) {
            PCF.logger.debug(
                    "COMMAND_ARGUMENT_TYPE registry has no entry for type: "
                            + serializer.getClass().getName());
            return;
        }

        if (PCF.isIntegratedArgument(identifier.toString())) {
            return;
        }

        // Not a standard Minecraft argument type - so we need to wrap it
        // NOTE: minecraft:command_argument_type is a Forge arg type
        PCF.logger.debug("Wrapping argument with identifier: " + identifier);
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
        buf.writeVarInt(PCF.commandArgumentTypeId(serializer));

        FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
        serializer.serializeToNetwork((T) properties, extraData);

        buf.writeVarInt(extraData.readableBytes());
        buf.writeBytes(extraData);

        extraData.release();
    }
}
