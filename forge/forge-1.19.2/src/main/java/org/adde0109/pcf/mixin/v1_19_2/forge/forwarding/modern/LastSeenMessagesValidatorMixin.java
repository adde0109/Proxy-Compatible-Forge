package org.adde0109.pcf.mixin.v1_19_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.LastSeenMessagesValidator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumSet;
import java.util.Set;

/**
 * Adapted from: <br>
 * <a
 * href="https://github.com/OKTW-Network/FabricProxy-Lite/blob/1a82df0b9f0ef41553ed3cb2f2a26ddf962d3d13/src/main/java/one/oktw/mixin/hack/AcknowledgmentValidator_SkipFirstMessageValidation.java">FabricProxy-Lite
 * 2.3.0</a>
 */
@ReqMappings(Mappings.SEARGE)
@ReqMCVersion(min = MinecraftVersion.V19_1, max = MinecraftVersion.V19_2)
@Mixin(LastSeenMessagesValidator.class)
public class LastSeenMessagesValidatorMixin {
    @Unique private boolean pcf$firstMessage = true;

    @Redirect(
            method = "validateAndUpdate",
            at = @At(value = "INVOKE", target = "Ljava/util/EnumSet;add(Ljava/lang/Object;)Z"))
    private boolean allowUnknownMessage(
            EnumSet<LastSeenMessagesValidator.ErrorCondition> instance, Object o) {
        if (pcf$firstMessage && o == LastSeenMessagesValidator.ErrorCondition.UNKNOWN_MESSAGES)
            return false;
        return instance.add((LastSeenMessagesValidator.ErrorCondition) o);
    }

    @Inject(method = "validateAndUpdate", at = @At("RETURN"))
    private void firstMessageSanded(
            LastSeenMessages.Update acknowledgment,
            CallbackInfoReturnable<Set<LastSeenMessagesValidator.ErrorCondition>> cir) {
        pcf$firstMessage = false;
    }
}
