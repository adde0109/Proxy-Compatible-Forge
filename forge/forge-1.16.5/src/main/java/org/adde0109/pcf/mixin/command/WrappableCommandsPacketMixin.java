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

import org.adde0109.pcf.ArgumentTypesEntryUtil;
import org.adde0109.pcf.Initializer;
import org.adde0109.pcf.command.IMixinWrappableCommandPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

  public void write(FriendlyByteBuf buffer, boolean wrap) {
    Object2IntMap<CommandNode<SharedSuggestionProvider>> object2intmap = enumerateNodes(this.root);
    CommandNode<SharedSuggestionProvider>[] commandnode = getNodesInIdOrder(object2intmap);
    buffer.writeVarInt(commandnode.length);

    for(CommandNode<SharedSuggestionProvider> commandnode1 : commandnode) {
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
  private static Object2IntMap<CommandNode<SharedSuggestionProvider>> enumerateNodes(RootCommandNode<SharedSuggestionProvider> root) { return null; }
  @Shadow
  private static CommandNode<SharedSuggestionProvider>[] getNodesInIdOrder(Object2IntMap<CommandNode<SharedSuggestionProvider>> p_178807_) { return null; }

  @Shadow
  private static void writeNode(FriendlyByteBuf p_131872_, CommandNode<SharedSuggestionProvider> p_131873_, Map<CommandNode<SharedSuggestionProvider>, Integer> p_131874_) {}

  @Inject(method = "writeNode(Lnet/minecraft/network/FriendlyByteBuf;Lcom/mojang/brigadier/tree/CommandNode;Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/synchronization/ArgumentTypes;serialize(Lnet/minecraft/network/FriendlyByteBuf;Lcom/mojang/brigadier/arguments/ArgumentType;)V"), cancellable = true)
  private static void writeNode$cancelArgumentSerialization(CallbackInfo ci) {
    ci.cancel();
  }

  private static final ResourceLocation MOD_ARGUMENT_INDICATOR = new ResourceLocation("crossstitch:mod_argument");

  private static <T extends ArgumentType<?>> void serialize(FriendlyByteBuf buf, T type, boolean wrap) {
    Object entry = ArgumentTypesEntryUtil.getEntry(type);

    if (entry == null) {
      buf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);
      buf.writeResourceLocation(new ResourceLocation("")); //"minecraft:"
      buf.writeVarInt(0);
      return;
    }

    if (!wrap || Initializer.integratedArgumentTypes.contains(ArgumentTypesEntryUtil.getName(entry).toString())) {
      ArgumentTypes.serialize(buf, type);
      return;
    }

    // Not a standard Minecraft argument type - so we need to wrap it
    serializeWrappedArgumentType(buf, type, entry);
  }

    private static <T extends ArgumentType<?>> void serializeWrappedArgumentType(FriendlyByteBuf packetByteBuf, T argumentType, Object entry) {
      packetByteBuf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);
      packetByteBuf.writeResourceLocation(ArgumentTypesEntryUtil.getName(entry));

      FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
      ArgumentTypesEntryUtil.getSerializer(entry).serializeToNetwork(argumentType, extraData);

      packetByteBuf.writeVarInt(extraData.readableBytes());
      packetByteBuf.writeBytes(extraData);
    }
  }
