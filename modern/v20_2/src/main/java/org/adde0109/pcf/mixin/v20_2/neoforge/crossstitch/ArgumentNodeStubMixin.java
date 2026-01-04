package org.adde0109.pcf.mixin.v20_2.neoforge.crossstitch;

import static org.adde0109.pcf.v20_2.neoforge.crossstitch.CrossStitchUtil19.writeNode$wrapInVelocityModArgument19;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import io.netty.buffer.ByteBuf;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;

import org.adde0109.pcf.PCF;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V20_2))
@Mixin(ClientboundCommandsPacket.ArgumentNodeStub.class)
public class ArgumentNodeStubMixin {
    // spotless:off
    @Inject(cancellable = true, at = @At("HEAD"),
            method = "serializeCap(Lnet/minecraft/network/FriendlyByteBuf;Lnet/minecraft/commands/synchronization/ArgumentTypeInfo;Lnet/minecraft/commands/synchronization/ArgumentTypeInfo$Template;)V")
    // spotless:on
    private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>>
            void writeNode$wrapInVelocityModArgument(
                    final @NotNull @Coerce ByteBuf buf,
                    final @NotNull ArgumentTypeInfo<A, T> serializer,
                    final @NotNull ArgumentTypeInfo.Template<A> properties,
                    final @NotNull CallbackInfo ci) {
        try {
            writeNode$wrapInVelocityModArgument19(buf, serializer, properties, ci);
        } catch (Exception e) {
            PCF.logger.error(
                    "Failed to serialize command argument type: " + serializer.getClass().getName(),
                    e);
        }
    }
}
