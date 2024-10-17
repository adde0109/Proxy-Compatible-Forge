package org.adde0109.pcf.mixin.v1_16_5.forge.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import dev.neuralnexus.conditionalmixins.annotations.ReqMCVersion;
import dev.neuralnexus.conditionalmixins.annotations.ReqMappings;
import dev.neuralnexus.taterapi.Mappings;
import dev.neuralnexus.taterapi.MinecraftVersion;

import io.netty.buffer.Unpooled;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.reflection.ArgumentTypesEntryUtil;
import org.adde0109.pcf.v1_14_4.forge.command.IMixinWrappableCommandPacket;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@ReqMappings(Mappings.SEARGE)
@ReqMCVersion(MinecraftVersion.V1_16_5)
@Mixin(ClientboundCommandsPacket.class)
@Implements(
        @Interface(
                iface = IMixinWrappableCommandPacket.class,
                prefix = "wcp$",
                remap = Interface.Remap.NONE))
public class WrappableCommandsPacketMixin {
    @Shadow private RootCommandNode<SharedSuggestionProvider> root;

    @Inject(
            method = "write(Lnet/minecraft/network/FriendlyByteBuf;)V",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void write(FriendlyByteBuf byteBuf, CallbackInfo ci) {
        ((IMixinWrappableCommandPacket) this).write(byteBuf, false);
        ci.cancel();
    }

    @SuppressWarnings({"DataFlowIssue", "deprecation", "rawtypes", "unchecked"})
    public void wcp$write(FriendlyByteBuf byteBuf, boolean wrap) {
        Object2IntMap<CommandNode<SharedSuggestionProvider>> object2intmap =
                enumerateNodes(this.root);
        CommandNode<SharedSuggestionProvider>[] commandnode = getNodesInIdOrder(object2intmap);
        byteBuf.writeVarInt(commandnode.length);

        for (CommandNode<SharedSuggestionProvider> commandnode1 : commandnode) {
            writeNode(byteBuf, commandnode1, object2intmap);
            if (commandnode1 instanceof ArgumentCommandNode argumentCommandNode) {
                pcf$serialize(byteBuf, argumentCommandNode.getType(), wrap);
                if (argumentCommandNode.getCustomSuggestions() != null) {
                    byteBuf.writeResourceLocation(
                            SuggestionProviders.getName(
                                    argumentCommandNode.getCustomSuggestions()));
                }
            }
        }

        byteBuf.writeVarInt(object2intmap.get(this.root));
    }

    @Shadow
    private static Object2IntMap<CommandNode<SharedSuggestionProvider>> enumerateNodes(
            RootCommandNode<SharedSuggestionProvider> root) {
        return null;
    }

    @Shadow
    private static CommandNode<SharedSuggestionProvider>[] getNodesInIdOrder(
            Object2IntMap<CommandNode<SharedSuggestionProvider>> p_178807_) {
        return null;
    }

    @Shadow
    private static void writeNode(
            FriendlyByteBuf p_131872_,
            CommandNode<SharedSuggestionProvider> p_131873_,
            Map<CommandNode<SharedSuggestionProvider>, Integer> p_131874_) {}

    // spotless:off
    @Inject(method = "writeNode(Lnet/minecraft/network/FriendlyByteBuf;Lcom/mojang/brigadier/tree/CommandNode;Ljava/util/Map;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/synchronization/ArgumentTypes;serialize(Lnet/minecraft/network/FriendlyByteBuf;Lcom/mojang/brigadier/arguments/ArgumentType;)V"), cancellable = true)
    private static void writeNode$cancelArgumentSerialization(CallbackInfo ci) {
        ci.cancel();
    }
    // spotless:on

    @Unique private static final ResourceLocation pcf$MOD_ARGUMENT_INDICATOR =
            new ResourceLocation("crossstitch:mod_argument");

    @Unique private static <T extends ArgumentType<?>> void pcf$serialize(
            FriendlyByteBuf buf, T type, boolean wrap) {
        Object entry = ArgumentTypesEntryUtil.getEntry(type);

        if (entry == null) {
            buf.writeResourceLocation(pcf$MOD_ARGUMENT_INDICATOR);
            buf.writeResourceLocation(new ResourceLocation("")); // "minecraft:"
            buf.writeVarInt(0);
            return;
        }

        String identifier = ArgumentTypesEntryUtil.getName(entry).toString();
        if (!wrap || PCF.isIntegratedArgument(identifier)) {
            ArgumentTypes.serialize(buf, type);
            return;
        }

        // Not a standard Minecraft argument type - so we need to wrap it
        PCF.logger.debug("Wrapping argument node stub with identifier: " + identifier);
        pcf$serializeWrappedArgumentType(buf, type, entry);
    }

    @SuppressWarnings({"unchecked", "VulnerableCodeUsages"})
    @Unique private static <T extends ArgumentType<?>> void pcf$serializeWrappedArgumentType(
            FriendlyByteBuf packetByteBuf, T argumentType, Object entry) {
        packetByteBuf.writeResourceLocation(pcf$MOD_ARGUMENT_INDICATOR);
        packetByteBuf.writeResourceLocation(
                (ResourceLocation) ArgumentTypesEntryUtil.getName(entry));

        FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
        ((ArgumentSerializer<T>) ArgumentTypesEntryUtil.getSerializer(entry))
                .serializeToNetwork(argumentType, extraData);

        packetByteBuf.writeVarInt(extraData.readableBytes());
        packetByteBuf.writeBytes(extraData);
    }
}
