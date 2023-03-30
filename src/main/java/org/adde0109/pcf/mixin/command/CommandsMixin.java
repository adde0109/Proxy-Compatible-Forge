package org.adde0109.pcf.mixin.command;

import io.netty.buffer.Unpooled;
import io.netty.util.AttributeKey;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.FMLConnectionData;
import org.adde0109.pcf.command.IMixinWrappableCommandPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.command.Commands.class)
public class CommandsMixin {
  @Redirect(method = "sendCommands(Lnet/minecraft/entity/player/ServerPlayerEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/ServerPlayNetHandler;send(Lnet/minecraft/network/IPacket;)V"))
  private void sendCommands$grabPacket(ServerPlayNetHandler connection, IPacket<?> packet) {
    FMLConnectionData connectionData = (FMLConnectionData) connection.connection.channel().attr(AttributeKey.valueOf("fml:conndata")).get();
    if (connectionData.getChannels().keySet().stream().anyMatch((v) -> v.equals(new ResourceLocation("ambassador:commands")))) {
      PacketBuffer byteBuf = new PacketBuffer(Unpooled.buffer());
      ((IMixinWrappableCommandPacket)(Object) packet).write(byteBuf, true);
      connection.send(new SCustomPayloadPlayPacket(new ResourceLocation("ambassador:commands"), byteBuf));
    } else {
      connection.send(packet);
    }
  }
}
