package org.adde0109.pcf.mixin.login;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import org.adde0109.pcf.Initializer;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerLoginNetHandler.class)
public class ModernForwardingMixin {


  @Final
  @Shadow
  public NetworkManager connection;

  @Shadow
  private GameProfile gameProfile;

  @Shadow
  public void disconnect(ITextComponent p_194026_1_) {}

  @Shadow
  private ServerLoginNetHandler.State state;

  private static final ResourceLocation VELOCITY_RESOURCE = new ResourceLocation("velocity:player_info");
  private boolean ambassador$listen = false;

  @Inject(method = "handleHello", at = @At("HEAD"), cancellable = true)
  private void onHandleHello(CallbackInfo ci) {
    Validate.validState(state == ServerLoginNetHandler.State.HELLO, "Unexpected hello packet");
    if(Initializer.modernForwardingInstance != null) {
      this.state = ServerLoginNetHandler.State.HELLO;
      LogManager.getLogger().debug("Sent Forward Request");
      this.connection.send(NetworkDirection.LOGIN_TO_CLIENT.buildPacket(Pair.of(new PacketBuffer(Unpooled.EMPTY_BUFFER),100),VELOCITY_RESOURCE).getThis());
      ambassador$listen = true;
      ci.cancel();
    }
  }

  @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
  private void onHandleCustomQueryPacket(CCustomPayloadLoginPacket p_209526_1_, CallbackInfo ci) {
    if((p_209526_1_.getIndex() == 100) && state == ServerLoginNetHandler.State.HELLO && ambassador$listen) {
      ambassador$listen = false;
      try {
        this.gameProfile = Initializer.modernForwardingInstance.handleForwardingPacket(p_209526_1_, connection);
        arclight$preLogin();
        this.state = ServerLoginNetHandler.State.NEGOTIATING;
      } catch (Exception e) {
        this.disconnect(new StringTextComponent("Direct connections to this server are not permitted!"));
        LogManager.getLogger().warn("Exception verifying forwarded player info", e);
      }
      ci.cancel();
    }
  }

  @Shadow(remap = false)
  private void arclight$preLogin() throws Exception {}

}
