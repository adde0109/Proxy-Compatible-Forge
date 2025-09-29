package org.adde0109.pcf.mixin.v1_17_1.forge.crossstitch;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_17_1.forge.crossstitch.CrossStitchUtil17;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@ReqMappings(Mappings.SEARGE)
@ReqMCVersion(min = MinecraftVersion.V17, max = MinecraftVersion.V18_2)
@Mixin(ClientboundCommandsPacket.class)
public class CommandsPacketMixin {
    // spotless:off
    @Redirect(method = "writeNode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/synchronization/ArgumentTypes;serialize(Lnet/minecraft/network/FriendlyByteBuf;Lcom/mojang/brigadier/arguments/ArgumentType;)V"))
    // spotless:on
    private static void writeNode$wrapInVelocityModArgument(
            FriendlyByteBuf buf, ArgumentType<?> argumentType) {
        try {
            CrossStitchUtil17.writeNode$wrapInVelocityModArgument17(buf, argumentType);
        } catch (Exception e) {
            PCF.logger.error("Failed to serialize command argument type: " + argumentType, e);
        }
    }
}
