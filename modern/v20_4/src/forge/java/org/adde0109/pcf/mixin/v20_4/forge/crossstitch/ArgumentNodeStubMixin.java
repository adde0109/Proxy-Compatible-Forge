package org.adde0109.pcf.mixin.v20_4.forge.crossstitch;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.protocol.game.ClientboundCommandsPacket;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.crossstitch.CrossStitch;
import org.adde0109.pcf.crossstitch.SerializerBridge;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@AConstraint(
        mappings = Mappings.SEARGE,
        version = @Versions(min = MinecraftVersion.V19, max = MinecraftVersion.V20_4))
@Mixin(ClientboundCommandsPacket.ArgumentNodeStub.class)
public class ArgumentNodeStubMixin {
    // spotless:off
    @Inject(cancellable = true, at = @At("HEAD"),
            method = "serializeCap(Lnet/minecraft/network/FriendlyByteBuf;Lnet/minecraft/commands/synchronization/ArgumentTypeInfo;Lnet/minecraft/commands/synchronization/ArgumentTypeInfo$Template;)V")
    // spotless:on
    private static void writeNode$wrapInVelocityModArgument(
            final @NotNull @Coerce ByteBuf buf,
            final @NotNull @Coerce SerializerBridge serializer,
            final @NotNull @Coerce Object properties,
            final @NotNull CallbackInfo ci) {
        try {
            CrossStitch.writeNode$wrapInVelocityModArgument(buf, serializer, properties, ci);
        } catch (Exception e) {
            PCF.logger.error(
                    "Failed to serialize command argument type: " + serializer.getClass().getName(),
                    e);
        }
    }
}
