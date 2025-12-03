package org.adde0109.pcf.mixin.v1_16_5.forge.crossstitch;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.FByteBuf;
import org.adde0109.pcf.v1_14_4.forge.crossstitch.CrossStitchUtil14;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Adapted from <a
 * href="https://github.com/VelocityPowered/CrossStitch/blob/fe3f3be49c52dc9c1b6b7cd3cafefb953adf4486/src/main/java/com/velocitypowered/crossstitch/mixin/command/CommandTreeSerializationMixin.java">CrossStitch</a>
 */
@AConstraint(mappings = Mappings.LEGACY_SEARGE, version = @Versions(MinecraftVersion.V16_5))
@Mixin(ClientboundCommandsPacket.class)
public abstract class CommandsPacketMixin {
    // spotless:off
    @Redirect(method = "writeNode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/synchronization/ArgumentTypes;serialize(Lnet/minecraft/network/FriendlyByteBuf;Lcom/mojang/brigadier/arguments/ArgumentType;)V"))
    private static void writeNode$wrapInVelocityModArgument(FriendlyByteBuf buf, ArgumentType<?> argumentType) {
        try {
            CrossStitchUtil14.writeNode$wrapInVelocityModArgument14(FByteBuf.wrap(buf), argumentType);
        } catch (Exception e) {
            PCF.logger.error("Failed to serialize command argument type: " + argumentType.getClass().getName(), e);
        }
    }
    // spotless:on
}
