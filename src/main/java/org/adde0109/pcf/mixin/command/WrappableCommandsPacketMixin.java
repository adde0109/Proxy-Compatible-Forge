package org.adde0109.pcf.mixin.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.util.ResourceLocation;
import org.adde0109.pcf.Initializer;
import org.adde0109.pcf.command.IMixinWrappableCommandPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;


@Mixin(SCommandListPacket.class)
public class WrappableCommandsPacketMixin implements IMixinWrappableCommandPacket {
  @Shadow
  @Final
  private RootCommandNode<ISuggestionProvider> root;

  @Inject(method = "write(Lnet/minecraft/network/PacketBuffer;)V", at = @At(value = "HEAD"), cancellable = true)
  public void write(PacketBuffer byteBuf, CallbackInfo ci) {
    write(byteBuf, false);
    ci.cancel();
  }

  public void write(PacketBuffer buffer, boolean wrap) {
    Object2IntMap<CommandNode<ISuggestionProvider>> object2intmap = enumerateNodes(this.root);
    CommandNode<ISuggestionProvider>[] commandnode = getNodesInIdOrder(object2intmap);
    buffer.writeVarInt(commandnode.length);

    for(CommandNode<ISuggestionProvider> commandnode1 : commandnode) {
      writeNode(buffer, commandnode1, object2intmap);
      if (commandnode1 instanceof ArgumentCommandNode) {
        ArgumentCommandNode argumentCommandNode = (ArgumentCommandNode) commandnode1;
        serialize(buffer, argumentCommandNode.getType(), wrap);
        if (argumentCommandNode.getCustomSuggestions() != null) {
          buffer.writeResourceLocation(SuggestionProviders.getName(argumentCommandNode.getCustomSuggestions()));
        }
      }
    }

    buffer.writeVarInt(object2intmap.get(this.root));
  }

  @Shadow
  private static Object2IntMap<CommandNode<ISuggestionProvider>> enumerateNodes(RootCommandNode<ISuggestionProvider> root) { return null; }
  @Shadow
  private static CommandNode<ISuggestionProvider>[] getNodesInIdOrder(Object2IntMap<CommandNode<ISuggestionProvider>> p_178807_) { return null; }

  @Shadow
  private static void writeNode(PacketBuffer p_131872_, CommandNode<ISuggestionProvider> p_131873_, Map<CommandNode<ISuggestionProvider>, Integer> p_131874_) {}

  @Inject(method = "writeNode(Lnet/minecraft/network/PacketBuffer;Lcom/mojang/brigadier/tree/CommandNode;Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/arguments/ArgumentTypes;serialize(Lnet/minecraft/network/PacketBuffer;Lcom/mojang/brigadier/arguments/ArgumentType;)V"), cancellable = true)
  private static void writeNode$cancelArgumentSerialization(CallbackInfo ci) {
    ci.cancel();
  }

  private static final ResourceLocation MOD_ARGUMENT_INDICATOR = new ResourceLocation("crossstitch:mod_argument");

  private static <T extends ArgumentType<?>> void serialize(PacketBuffer buf, T type, boolean wrap) {
    ArgumentTypes.Entry<T> entry = (ArgumentTypes.Entry<T>)ArgumentTypes.get(type);

    if (entry == null) {
      buf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);
      buf.writeResourceLocation(new ResourceLocation("")); //"minecraft:"
      buf.writeVarInt(0);
      return;
    }

    if (!wrap || Initializer.integratedArgumentTypes.contains(entry.name.toString())) {
      ArgumentTypes.serialize(buf, type);
      return;
    }

    // Not a standard Minecraft argument type - so we need to wrap it
    serializeWrappedArgumentType(buf, type, entry);
  }

  private static <T extends ArgumentType<?>> void serializeWrappedArgumentType(PacketBuffer packetByteBuf, T argumentType, ArgumentTypes.Entry<T> entry) {
    packetByteBuf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);
    packetByteBuf.writeResourceLocation(entry.name);

    PacketBuffer extraData = new PacketBuffer(Unpooled.buffer());
    entry.serializer.serializeToNetwork(argumentType, extraData);

    packetByteBuf.writeVarInt(extraData.readableBytes());
    packetByteBuf.writeBytes(extraData);
  }
}
