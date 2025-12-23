package org.adde0109.pcf.v14_4.forge.crossstitch;

import static org.adde0109.pcf.v14_4.forge.crossstitch.CSBootstrap.shouldWrapArgument;

import com.mojang.brigadier.arguments.ArgumentType;

import io.netty.buffer.Unpooled;

import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.FriendlyByteBuf;

/**
 * Adapted from <a
 * href="https://github.com/VelocityPowered/CrossStitch/blob/fe3f3be49c52dc9c1b6b7cd3cafefb953adf4486/src/main/java/com/velocitypowered/crossstitch/mixin/command/CommandTreeSerializationMixin.java">CrossStitch</a>
 */
public final class CrossStitchUtil14 {
    private static final ResourceLocation MOD_ARGUMENT_INDICATOR =
            new ResourceLocation("crossstitch:mod_argument");
    private static final ResourceLocation EMPTY_IDENTIFIER = new ResourceLocation("");
    private static final int ZERO_LENGTH = 0;

    @SuppressWarnings("unchecked")
    public static void writeNode$wrapInVelocityModArgument14(
            FriendlyByteBuf buf, ArgumentType<?> argumentType) {
        if (!PCF.instance().crossStitch().enabled()) {
            return;
        }
        Object entry = ArgumentTypesUtil.getEntry(argumentType);

        if (entry == null) {
            PCF.logger.debug(
                    "Wrapping entryless argument type: " + argumentType.getClass().getName());
            buf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);
            buf.writeResourceLocation(EMPTY_IDENTIFIER);
            buf.writeVarInt(ZERO_LENGTH);
            return;
        }

        ResourceLocation identifier = (ResourceLocation) ArgumentTypesUtil.getName(entry);
        if (!shouldWrapArgument(identifier)) {
            buf.writeResourceLocation(identifier);
            ((ArgumentSerializer<ArgumentType<?>>) ArgumentTypesUtil.getSerializer(entry))
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
            FriendlyByteBuf buf, ArgumentType<?> argumentType, Object entry) {
        buf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);
        buf.writeResourceLocation(ArgumentTypesUtil.getName(entry));

        net.minecraft.network.FriendlyByteBuf extraData =
                new net.minecraft.network.FriendlyByteBuf(Unpooled.buffer());
        ((ArgumentSerializer<ArgumentType<?>>) ArgumentTypesUtil.getSerializer(entry))
                .serializeToNetwork(argumentType, extraData);

        buf.writeVarInt(extraData.readableBytes());
        buf.writeBytes(extraData);

        extraData.release();
    }
}
