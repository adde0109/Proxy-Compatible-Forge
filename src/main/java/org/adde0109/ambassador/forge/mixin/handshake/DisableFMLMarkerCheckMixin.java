package org.adde0109.ambassador.forge.mixin.handshake;

import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientIntentionPacket.class)
public class DisableFMLMarkerCheckMixin {


  @Shadow
  private String fmlVersion = net.minecraftforge.network.NetworkConstants.NETVERSION;

  @Inject(method = "<init>", at = @At("RETURN"), cancellable = true)
  private void onGetFMLVersion(CallbackInfo ci) {
    this.fmlVersion = net.minecraftforge.network.NetworkConstants.NETVERSION;
  }

}
