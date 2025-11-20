package org.adde0109.pcf.mixin.v1_19_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.chat.ChatMessageContent;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSigner;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignedMessageChain;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adapted from: <br>
 * <a
 * href="https://github.com/OKTW-Network/FabricProxy-Lite/blob/1a82df0b9f0ef41553ed3cb2f2a26ddf962d3d13/src/main/java/one/oktw/mixin/hack/MessageChain_SkipFirstMessageValidation.java">FabricProxy-Lite
 * 2.3.0</a>
 */
@AConstraint(
        mappings = Mappings.SEARGE,
        version = @Versions(min = MinecraftVersion.V19_1, max = MinecraftVersion.V19_2))
@Mixin(SignedMessageChain.class)
public class SignedMessageChainMixin {
    @Shadow private @Nullable MessageSignature previousSignature;
    @Unique private boolean pcf$firstMessage = true;

    @Inject(
            method =
                    "unpack(Lnet/minecraft/network/chat/SignedMessageChain$Link;Lnet/minecraft/network/chat/MessageSigner;Lnet/minecraft/network/chat/ChatMessageContent;Lnet/minecraft/network/chat/LastSeenMessages;)Lnet/minecraft/network/chat/PlayerChatMessage;",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void allowUnknownMessage(
            SignedMessageChain.Link signature,
            MessageSigner metadata,
            ChatMessageContent contents,
            LastSeenMessages lastSeenMessages,
            CallbackInfoReturnable<PlayerChatMessage> cir) {
        if (!pcf$firstMessage) return;

        pcf$firstMessage = false;
        if (!lastSeenMessages.entries().isEmpty()) {
            previousSignature = signature.signature();
            cir.setReturnValue(PlayerChatMessage.system(contents));
        }
    }
}
