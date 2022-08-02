package org.adde0109.ambassador.forge.mixin.handshake;

import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientIntentionPacket.class, priority = 1200)
public class DisableFMLMarkerCheckMixin {

  @Inject(method = "getFMLVersion", at = @At("HEAD"), remap = false, cancellable = true)
  private void onGetFMLVersion(CallbackInfoReturnable<String> cir) {
    cir.setReturnValue(net.minecraftforge.network.NetworkConstants.NETVERSION);
  }

}
