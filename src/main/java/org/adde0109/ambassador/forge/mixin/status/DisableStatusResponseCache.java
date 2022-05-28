package org.adde0109.ambassador.forge.mixin.status;


import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.server.SServerInfoPacket;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(SServerInfoPacket.class)
public class DisableStatusResponseCache {


  @Shadow
  private ServerStatusResponse status;

  @Inject(method = "write", at = @At("HEAD"))
  public void onWrite(CallbackInfo ci) throws IOException {
    this.status.invalidateJson();
    LogManager.getLogger().warn("Write attempt!");
  }
}
