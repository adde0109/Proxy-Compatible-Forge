package org.adde0109.pcf.mixin.v1_14_4.forge.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import io.netty.buffer.Unpooled;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_14_4.forge.reflection.ArgumentTypesEntryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Adapted from <a
 * href="https://github.com/VelocityPowered/CrossStitch/blob/fe3f3be49c52dc9c1b6b7cd3cafefb953adf4486/src/main/java/com/velocitypowered/crossstitch/mixin/command/CommandTreeSerializationMixin.java">CrossStitch</a>
 */
@ReqMappings(Mappings.LEGACY_SEARGE)
@ReqMCVersion(min = MinecraftVersion.V14_4, max = MinecraftVersion.V16_4)
@Mixin(ClientboundCommandsPacket.class)
public abstract class ClientboundCommandsPacketMixin {
    // spotless:off
    @Unique private static final ResourceLocation pcf$MOD_ARGUMENT_INDICATOR = new ResourceLocation("crossstitch:mod_argument");
    @Unique private static final ResourceLocation pcf$FORGE_ENUM = new ResourceLocation("forge:enum");
    @Unique private static final ResourceLocation pcf$FORGE_MODID = new ResourceLocation("forge:modid");

    @Inject(cancellable = true, method = "writeNode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/synchronization/ArgumentTypes;serialize(Lnet/minecraft/network/FriendlyByteBuf;Lcom/mojang/brigadier/arguments/ArgumentType;)V"))
    // spotless:on
    public void writeNode$wrapInVelocityModArgument(
            FriendlyByteBuf buf,
            CommandNode<SharedSuggestionProvider> node,
            Map<CommandNode<SharedSuggestionProvider>, Integer> map,
            CallbackInfo ci) {
        ArgumentCommandNode<SharedSuggestionProvider, ?> argNode =
                (ArgumentCommandNode<SharedSuggestionProvider, ?>) node;
        ArgumentType<?> argType = argNode.getType();

        // Forge didn't implement any serializers?
        if (argType.getClass() == EnumArgument.class || argType.getClass() == ModIdArgument.class) {
            ResourceLocation identifier =
                    switch (argType) {
                        case EnumArgument<?> ignored -> pcf$FORGE_ENUM;
                        case ModIdArgument ignored -> pcf$FORGE_MODID;
                        default -> throw new IllegalStateException("Unexpected value: " + argType);
                    };
            PCF.logger.debug("Detected Forge argument type: " + identifier);
            buf.writeResourceLocation(pcf$MOD_ARGUMENT_INDICATOR);
            buf.writeResourceLocation(identifier);
            buf.writeVarInt(0);
            ci.cancel();
            return;
        }

        // ArgumentTypes.Entry<?> entry = ArgumentTypes.BY_CLASS.get(argumentType.getClass());
        Object entry = ArgumentTypesEntryUtil.getEntry(argType);
        if (entry == null) {
            PCF.logger.debug(
                    "ArgumentTypes has no entry for type: " + argType.getClass().getName());
            return;
        }
        ResourceLocation identifier = ArgumentTypesEntryUtil.getName(entry);
        if (PCF.isIntegratedArgument(identifier.toString())) {
            return;
        }

        // Not a standard Minecraft argument type - so we need to wrap it
        PCF.logger.debug("Wrapping argument with identifier: " + identifier);
        pcf$serializeWrappedArgumentType(buf, argType, entry);
        ci.cancel();
    }

    @Unique private static void pcf$serializeWrappedArgumentType(
            FriendlyByteBuf buf, ArgumentType<?> argumentType, Object entry) {
        buf.writeResourceLocation(pcf$MOD_ARGUMENT_INDICATOR);

        buf.writeResourceLocation(ArgumentTypesEntryUtil.getName(entry));

        FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
        ArgumentTypesEntryUtil.getSerializer(entry).serializeToNetwork(argumentType, extraData);

        buf.writeVarInt(extraData.readableBytes());
        buf.writeBytes(extraData);
    }
}
