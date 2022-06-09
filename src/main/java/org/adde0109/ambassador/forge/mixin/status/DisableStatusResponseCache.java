package org.adde0109.ambassador.forge.mixin.status;


import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ClientboundStatusResponsePacket.class)
public class DisableStatusResponseCache {


  @Shadow
  private ServerStatus status;

  @Inject(method = "write", at = @At("HEAD"))
  public void onWrite(CallbackInfo ci) throws IOException {
    this.status.invalidateJson();
  }
}
