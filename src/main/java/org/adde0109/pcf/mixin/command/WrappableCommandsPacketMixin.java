package org.adde0109.pcf.mixin.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.resources.ResourceLocation;
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

@Mixin(ClientboundCommandsPacket.class)
public class WrappableCommandsPacketMixin implements IMixinWrappableCommandPacket {
  @Shadow
  @Final
  private RootCommandNode<SharedSuggestionProvider> root;

  @Inject(method = "write(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "HEAD"), cancellable = true)
  public void write(FriendlyByteBuf byteBuf, CallbackInfo ci) {
    write(byteBuf, false);
    ci.cancel();
  }

  public void write(FriendlyByteBuf byteBuf, boolean wrap) {
    Object2IntMap<CommandNode<SharedSuggestionProvider>> object2intmap = enumerateNodes(this.root);
    List<CommandNode<SharedSuggestionProvider>> list = getNodesInIdOrder(object2intmap);
    byteBuf.writeCollection(list, (p_178810_, p_178811_) -> {
      writeNode(p_178810_, p_178811_, object2intmap);
      if (p_178811_ instanceof ArgumentCommandNode argumentCommandNode) {
          serialize(byteBuf, argumentCommandNode.getType(), wrap);
        if (argumentCommandNode.getCustomSuggestions() != null) {
          byteBuf.writeResourceLocation(SuggestionProviders.getName(argumentCommandNode.getCustomSuggestions()));
        }
      }
    });
    byteBuf.writeVarInt(object2intmap.get(this.root));
  }

  @Shadow
  private static Object2IntMap<CommandNode<SharedSuggestionProvider>> enumerateNodes(RootCommandNode<SharedSuggestionProvider> root) { return null; }
  @Shadow
  private static List<CommandNode<SharedSuggestionProvider>> getNodesInIdOrder(Object2IntMap<CommandNode<SharedSuggestionProvider>> p_178807_) { return null; }

  @Shadow
  private static void writeNode(FriendlyByteBuf p_131872_, CommandNode<SharedSuggestionProvider> p_131873_, Map<CommandNode<SharedSuggestionProvider>, Integer> p_131874_) {}

  @Inject(method = "writeNode(Lnet/minecraft/network/FriendlyByteBuf;Lcom/mojang/brigadier/tree/CommandNode;Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/synchronization/ArgumentTypes;serialize(Lnet/minecraft/network/FriendlyByteBuf;Lcom/mojang/brigadier/arguments/ArgumentType;)V"), cancellable = true)
  private static void writeNode$cancelArgumentSerialization(CallbackInfo ci) {
    ci.cancel();
  }

  private static final ResourceLocation MOD_ARGUMENT_INDICATOR = new ResourceLocation("crossstitch:mod_argument");

  private static <T extends ArgumentType<?>> void serialize(FriendlyByteBuf buf, T type, boolean wrap) {
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

    private static <T extends ArgumentType<?>> void serializeWrappedArgumentType(FriendlyByteBuf packetByteBuf, T argumentType, ArgumentTypes.Entry<T> entry) {
      packetByteBuf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);
      packetByteBuf.writeResourceLocation(entry.name);

      FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
      entry.serializer.serializeToNetwork(argumentType, extraData);

      packetByteBuf.writeVarInt(extraData.readableBytes());
      packetByteBuf.writeBytes(extraData);
    }
  }
