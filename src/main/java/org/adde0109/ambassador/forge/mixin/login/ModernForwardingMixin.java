package org.adde0109.ambassador.forge.mixin.login;

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
import org.adde0109.ambassador.forge.Ambassador;
import org.adde0109.ambassador.forge.ModernForwarding;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerLoginNetHandler.class)
public class ModernForwardingMixin {


  @Shadow
  private NetworkManager connection;

  @Shadow
  private GameProfile gameProfile;

  @Shadow
  private void disconnect(ITextComponent p_194026_1_) {}

  @Shadow
  private ServerLoginNetHandler.State state;

  private static final ResourceLocation VELOCITY_RESOURCE = new ResourceLocation("velocity:player_info");

  @Inject(method = "handleHello", at = @At("RETURN"))
  private void onHandleHello(CallbackInfo ci) {
    if(Ambassador.modernForwardingInstance != null) {
      this.state = ServerLoginNetHandler.State.HELLO;
      LogManager.getLogger().warn("Sent Forward Request");
      this.connection.send(NetworkDirection.LOGIN_TO_CLIENT.buildPacket(Pair.of(new PacketBuffer(Unpooled.EMPTY_BUFFER),100),VELOCITY_RESOURCE).getThis());
    }
  }

  @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
  private void onHandleCustomQueryPacket(CCustomPayloadLoginPacket p_209526_1_, CallbackInfo ci) {
    if((Ambassador.modernForwardingInstance != null) &&(p_209526_1_.getIndex() == 100)) {
      this.gameProfile = Ambassador.modernForwardingInstance.handleForwardingPacket(p_209526_1_);
      if(this.gameProfile == null) {
        this.disconnect(new StringTextComponent("Direct connections to this server are not permitted!"));
        LogManager.getLogger().error("Someone tried to join directly!");
      }
      this.state = ServerLoginNetHandler.State.NEGOTIATING;
      ci.cancel();
    }
  }

}
