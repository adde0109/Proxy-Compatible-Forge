package org.adde0109.pcf.mixin.v1_16_5.forge.command;

import dev.neuralnexus.conditionalmixins.annotations.ReqMCVersion;
import dev.neuralnexus.conditionalmixins.annotations.ReqMappings;
import dev.neuralnexus.taterapi.Mappings;
import dev.neuralnexus.taterapi.MinecraftVersion;

import io.netty.buffer.Unpooled;

import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.fml.network.FMLConnectionData;
import net.minecraftforge.fml.network.NetworkHooks;

import org.adde0109.pcf.v1_14_4.forge.command.IMixinWrappableCommandPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@ReqMappings(Mappings.SEARGE)
@ReqMCVersion(MinecraftVersion.V1_16_5)
@Mixin(Commands.class)
public class CommandsMixin {
    @Unique private static final ResourceLocation pcf$AMBASSADOR_COMMANDS =
            new ResourceLocation("ambassador:commands");

    // spotless:off
    @SuppressWarnings("VulnerableCodeUsages")
    @Redirect(method = "sendCommands(Lnet/minecraft/server/level/ServerPlayer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
    // spotless:on
    private void sendCommands$grabPacket(
            ServerGamePacketListenerImpl connection, Packet<?> packet) {
        FMLConnectionData connectionData = NetworkHooks.getConnectionData(connection.connection);
        if (connectionData != null
                && connectionData.getChannels().keySet().stream()
                        .anyMatch((v) -> v.equals(pcf$AMBASSADOR_COMMANDS))) {
            FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
            ((IMixinWrappableCommandPacket) packet).write(byteBuf, true);
            connection.send(new ClientboundCustomPayloadPacket(pcf$AMBASSADOR_COMMANDS, byteBuf));
        } else {
            connection.send(packet);
        }
    }
}
