package org.adde0109.pcf.mixin.v1_19_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.PlayerChatMessage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adapted from: <br>
 * <a
 * href="https://github.com/OKTW-Network/FabricProxy-Lite/blob/1a82df0b9f0ef41553ed3cb2f2a26ddf962d3d13/src/main/java/one/oktw/mixin/hack/SignedMessage_SkipFirstMessageValidation.java">FabricProxy-Lite
 * 2.3.0</a>
 */
@ReqMappings(Mappings.SEARGE)
@ReqMCVersion(min = MinecraftVersion.V19_1, max = MinecraftVersion.V19_2)
@Mixin(PlayerChatMessage.class)
public class PlayerChatMessageMixin {
    @Unique private boolean pcf$firstMessage = true;

    @Inject(
            method = "verify(Lnet/minecraft/network/chat/ChatSender;)Z",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void allowUnknownMessage(ChatSender profile, CallbackInfoReturnable<Boolean> cir) {
        if (pcf$firstMessage) {
            pcf$firstMessage = false;
            cir.setReturnValue(true);
        }
    }
}
