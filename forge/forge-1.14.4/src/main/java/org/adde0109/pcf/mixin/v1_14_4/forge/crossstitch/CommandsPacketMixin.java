package org.adde0109.pcf.mixin.v1_14_4.forge.crossstitch;

import com.mojang.brigadier.tree.CommandNode;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_14_4.forge.crossstitch.CrossStitchUtil;
import org.spongepowered.asm.mixin.Mixin;
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
public abstract class CommandsPacketMixin {
    // spotless:off
    @Inject(cancellable = true, method = "writeNode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/synchronization/ArgumentTypes;serialize(Lnet/minecraft/network/FriendlyByteBuf;Lcom/mojang/brigadier/arguments/ArgumentType;)V"))
    // spotless:on
    public void writeNode$wrapInVelocityModArgument(
            FriendlyByteBuf buf,
            CommandNode<SharedSuggestionProvider> node,
            Map<CommandNode<SharedSuggestionProvider>, Integer> map,
            CallbackInfo ci) {
        try {
            CrossStitchUtil.writeNode$wrapInVelocityModArgument(buf, node, map, ci);
        } catch (Exception e) {
            PCF.logger.error(
                    "Failed to serialize command argument type: " + node.getClass().getName(), e);
        }
    }
}
