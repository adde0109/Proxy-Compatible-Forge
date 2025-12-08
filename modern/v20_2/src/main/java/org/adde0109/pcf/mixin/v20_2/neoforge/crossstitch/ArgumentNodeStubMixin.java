package org.adde0109.pcf.mixin.v20_2.neoforge.crossstitch;

import static org.adde0109.pcf.v20_2.neoforge.crossstitch.CrossStitchUtil19.writeNode$wrapInVelocityModArgument19;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.FByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V20_2))
@Mixin(targets = "net.minecraft.network.protocol.game.ClientboundCommandsPacket$ArgumentNodeStub")
public class ArgumentNodeStubMixin {
    // spotless:off
    @Inject(cancellable = true, at = @At("HEAD"),
            method = "serializeCap(Lnet/minecraft/network/FriendlyByteBuf;Lnet/minecraft/commands/synchronization/ArgumentTypeInfo;Lnet/minecraft/commands/synchronization/ArgumentTypeInfo$Template;)V")
    private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>>
            void writeNode$wrapInVelocityModArgument(
                    FriendlyByteBuf buf,
                    ArgumentTypeInfo<A, T> serializer,
                    ArgumentTypeInfo.Template<A> properties,
                    CallbackInfo ci) {
        try {
            writeNode$wrapInVelocityModArgument19(FByteBuf.wrap(buf), serializer, properties, ci);
        } catch (Exception e) {
            PCF.logger.error("Failed to serialize command argument type: " + serializer.getClass().getName(), e);
        }
    }
    // spotless:on
}
