package org.adde0109.pcf.v1_14_4.forge.crossstitch;

import static org.adde0109.pcf.v1_14_4.forge.crossstitch.CSBootstrap.shouldWrapArgument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

import io.netty.buffer.Unpooled;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCF;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Adapted from <a
 * href="https://github.com/VelocityPowered/CrossStitch/blob/fe3f3be49c52dc9c1b6b7cd3cafefb953adf4486/src/main/java/com/velocitypowered/crossstitch/mixin/command/CommandTreeSerializationMixin.java">CrossStitch</a>
 */
public final class CrossStitchUtil14 {
    private static final ResourceLocation MOD_ARGUMENT_INDICATOR =
            new ResourceLocation("crossstitch:mod_argument");

    public static void writeNode$wrapInVelocityModArgument(
            FriendlyByteBuf buf,
            CommandNode<SharedSuggestionProvider> node,
            Map<CommandNode<SharedSuggestionProvider>, Integer> ignored,
            CallbackInfo ci) {
        if (!PCF.instance().crossStitch().enabled()) {
            return;
        }
        ArgumentCommandNode<SharedSuggestionProvider, ?> argNode =
                (ArgumentCommandNode<SharedSuggestionProvider, ?>) node;
        ArgumentType<?> argumentType = argNode.getType();

        Object entry = ArgumentTypesUtil.getEntry(argumentType);
        if (entry == null) {
            PCF.logger.debug(
                    "ArgumentTypes has no entry for type: " + argumentType.getClass().getName());
            return;
        }
        ResourceLocation identifier = (ResourceLocation) ArgumentTypesUtil.getName(entry);
        if (!shouldWrapArgument(identifier)) {
            if (PCF.instance().debug().enabled()) {
                PCF.logger.debug("Not wrapping argument with identifier: " + identifier);
            }
            return;
        }

        // Not a standard Minecraft argument type - so we need to wrap it
        PCF.logger.debug("Wrapping argument with identifier: " + identifier);
        serializeWrappedArgumentType(buf, argumentType, entry);
        ci.cancel();
    }

    @SuppressWarnings("unchecked")
    public static void serializeWrappedArgumentType(
            FriendlyByteBuf buf, ArgumentType<?> argumentType, Object entry) {
        buf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);
        buf.writeResourceLocation((ResourceLocation) ArgumentTypesUtil.getName(entry));

        FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
        ((ArgumentSerializer<ArgumentType<?>>) ArgumentTypesUtil.getSerializer(entry))
                .serializeToNetwork(argumentType, extraData);

        buf.writeVarInt(extraData.readableBytes());
        buf.writeBytes(extraData);

        extraData.release();
    }
}
