package org.adde0109.pcf.mixin.handshake;

import io.netty.util.AttributeKey;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;
import org.adde0109.pcf.IMixinPCFMarkerMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerHandshakePacketListenerImpl.class, priority = 300)
public class PCFNetworkAttribute {

  @Shadow
  private Connection connection;

  @Inject(method = "handleIntention", at = @At("HEAD"))
  private void handleIntention(ClientIntentionPacket p_9975_, CallbackInfo ci) {
    String marker = ((IMixinPCFMarkerMixin) (Object) p_9975_).getAmbassador$PCFMarker();
    connection.channel().attr(AttributeKey.valueOf("pcf:netversion")).set(marker);
  }
}
