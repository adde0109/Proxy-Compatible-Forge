package org.adde0109.pcf.mixin.command;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.adde0109.pcf.command.IMixinWrappableCommandPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(net.minecraft.commands.Commands.class)
public class CommandsMixin {

  @Redirect(method = "sendCommands(Lnet/minecraft/server/level/ServerPlayer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
  private void sendCommands$grabPacket(ServerGamePacketListenerImpl connection, Packet<?> packet) {
    FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
    ((IMixinWrappableCommandPacket)(Object) packet).wrapAndWrite(byteBuf);
    connection.send(new ClientboundCustomPayloadPacket(new ResourceLocation("pcf:commands"), byteBuf));
    connection.send(packet);
  }
}
