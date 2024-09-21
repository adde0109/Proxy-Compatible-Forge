package org.adde0109.pcf.mixin.command;

import io.netty.buffer.Unpooled;
import io.netty.util.AttributeKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.network.ConnectionData;
import org.adde0109.pcf.command.IMixinWrappableCommandPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.commands.Commands.class)
public class CommandsMixin {
  @Redirect(method = "sendCommands(Lnet/minecraft/server/level/ServerPlayer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
  private void sendCommands$grabPacket(ServerGamePacketListenerImpl connection, Packet<?> packet) {
    ConnectionData connectionData = (ConnectionData) connection.connection.channel().attr(AttributeKey.valueOf("fml:conndata")).get();
    if (connectionData != null && connectionData.getChannels().keySet().stream().anyMatch((v) -> v.equals(new ResourceLocation("ambassador:commands")))) {
      FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
      ((IMixinWrappableCommandPacket)(Object) packet).write(byteBuf, true);
      connection.send(new ClientboundCustomPayloadPacket(new ResourceLocation("ambassador:commands"), byteBuf));
    } else {
      connection.send(packet);
    }
  }
}
