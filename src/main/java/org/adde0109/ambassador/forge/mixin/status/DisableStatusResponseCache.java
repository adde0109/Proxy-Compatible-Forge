package org.adde0109.ambassador.forge.mixin.status;


import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundStatusResponsePacket.class)
public class DisableStatusResponseCache {


  @Shadow
  private ServerStatus status;

  @Inject(method = "write", at = @At("HEAD"))
  public void onWrite(CallbackInfo ci) {
    this.status.invalidateJson();
  }
}
