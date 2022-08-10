package org.adde0109.ambassador.forge.mixin.handshake;

import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CHandshakePacket.class, priority = 1200)
public class DisableFMLMarkerCheckMixin {


  @Inject(method = "getFMLVersion", at = @At(value = "HEAD"), remap = false, cancellable = true)
  private void onGetFMLVersion(CallbackInfoReturnable<String> cir) {
    cir.setReturnValue(FMLNetworkConstants.NETVERSION);
  }

}
