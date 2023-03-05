package org.adde0109.pcf.mixin.handshake;

import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import org.adde0109.pcf.IMixinPCFMarkerMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(value = ClientIntentionPacket.class, priority = 300)
public class PCFMarkerMixin implements IMixinPCFMarkerMixin {

  private static String EXTRA_DATA;
  private String ambassador$PCFMarker;

  @Redirect(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V",
          at = @At(value = "INVOKE", target = "Ljava/lang/String;split(Ljava/lang/String;)[Ljava/lang/String;"))
  private String[] ClientIntentionPacket(String og, String split) {
   if (EXTRA_DATA != null && EXTRA_DATA.split("\0").length >= 3) {
      ambassador$PCFMarker = EXTRA_DATA.split("\0")[2];
    } else if (og.split("\0").length >= 3) {
      ambassador$PCFMarker = og.split("\0")[2];
    }
    return og.split("\0");
  }

  public String getAmbassador$PCFMarker() {
    return ambassador$PCFMarker;
  }
}
