package org.adde0109.pcf.v1_17_1.forge.crossstitch;

import com.mojang.brigadier.arguments.ArgumentType;

import io.netty.buffer.Unpooled;

import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_14_4.forge.reflection.ArgumentTypesUtil;

/**
 * Adapted from <a
 * href="https://github.com/VelocityPowered/CrossStitch/blob/fe3f3be49c52dc9c1b6b7cd3cafefb953adf4486/src/main/java/com/velocitypowered/crossstitch/mixin/command/CommandTreeSerializationMixin.java">CrossStitch</a>
 */
public final class CrossStitchUtil17 {
    private static final ResourceLocation MOD_ARGUMENT_INDICATOR =
            new ResourceLocation("crossstitch:mod_argument");
    private static final ResourceLocation FORGE_ENUM = new ResourceLocation("forge:enum");
    private static final ResourceLocation FORGE_MODID = new ResourceLocation("forge:modid");

    @SuppressWarnings("unchecked")
    public static void writeNode$wrapInVelocityModArgument17(
            FriendlyByteBuf buf, ArgumentType<?> argumentType) {
        Object entry = ArgumentTypesUtil.getEntry(argumentType);

        if (entry == null) {
            PCF.logger.info(
                    "ArgumentTypes has no entry for type: " + argumentType.getClass().getName());
            buf.writeResourceLocation(new ResourceLocation(""));
            return;
        }

        ResourceLocation identifier = (ResourceLocation) ArgumentTypesUtil.getName(entry);
        if (PCF.isIntegratedArgument(identifier.toString())
                || FORGE_ENUM.equals(identifier)
                || FORGE_MODID.equals(identifier)) {
            buf.writeResourceLocation(identifier);
            ((ArgumentSerializer<ArgumentType<?>>) ArgumentTypesUtil.getSerializer(entry))
                    .serializeToNetwork(argumentType, buf);
            return;
        }

        // Not a standard Minecraft argument type - so we need to wrap it
        PCF.logger.info("Wrapping argument with identifier: " + identifier);
        serializeWrappedArgumentType(buf, argumentType, entry);
    }

    @SuppressWarnings("unchecked")
    private static void serializeWrappedArgumentType(
            FriendlyByteBuf buf, ArgumentType<?> argumentType, Object entry) {
        buf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);

        buf.writeResourceLocation((ResourceLocation) ArgumentTypesUtil.getName(entry));

        FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
        ((ArgumentSerializer<ArgumentType<?>>) ArgumentTypesUtil.getSerializer(entry))
                .serializeToNetwork(argumentType, extraData);

        buf.writeVarInt(extraData.readableBytes());
        buf.writeBytes(extraData);
    }
}
