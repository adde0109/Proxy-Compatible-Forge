//Code modified from https://github.com/VelocityPowered/CrossStitch

package org.adde0109.ambassador.forge.mixin.command;

import com.mojang.brigadier.arguments.ArgumentType;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.Arrays;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.protocol.game.ClientboundCommandsPacket$ArgumentNodeStub")
public class CommandTreeSerializationMixin {

    @Shadow @Final private ArgumentTypeInfo.Template<?> argumentType;
    private static final int MOD_ARGUMENT_INDICATOR = -256;

    private static final String[] ignore = {
            "brigadier:string",
            "brigadier:integer",
            "brigadier:float",
            "brigadier:double",
            "brigadier:bool",
            "brigadier:long",
            "minecraft:resource",
            "minecraft:resource_or_tag",
            "minecraft:entity",
            "minecraft:score_holder",
            "minecraft:game_profile",
            "minecraft:block_pos",
            "minecraft:column_pos",
            "minecraft:vec3",
            "minecraft:vec2",
            "minecraft:block_state",
            "minecraft:block_predicate",
            "minecraft:item_stack",
            "minecraft:item_predicate",
            "minecraft:color",
            "minecraft:component",
            "minecraft:message",
            "minecraft:nbt",
            "minecraft:nbt_compound_tag", // added in 1.14
            "minecraft:nbt_tag", // added in 1.14
            "minecraft:nbt_path",
            "minecraft:objective",
            "minecraft:objective_criteria",
            "minecraft:operation",
            "minecraft:particle",
            "minecraft:rotation",
            "minecraft:scoreboard_slot",
            "minecraft:swizzle",
            "minecraft:team",
            "minecraft:item_slot",
            "minecraft:resource_location",
            "minecraft:mob_effect",
            "minecraft:function",
            "minecraft:entity_anchor",
            "minecraft:item_enchantment",
            "minecraft:entity_summon",
            "minecraft:dimension",
            "minecraft:int_range",
            "minecraft:float_range",
            "minecraft:time", // added in 1.14
            "minecraft:uuid", // added in 1.16
            "minecraft:angle", // added in 1.16.2
            "minecraft:template_mirror", // 1.19
            "minecraft:template_rotation" // 1.19
    };

    @Inject(method = "serializeCap(Lnet/minecraft/network/FriendlyByteBuf;Lnet/minecraft/commands/synchronization/ArgumentTypeInfo;Lnet/minecraft/commands/synchronization/ArgumentTypeInfo$Template;)V",
            at = @At("HEAD"), cancellable = true)
    private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void writeNode$wrapInVelocityModArgument(FriendlyByteBuf buf, ArgumentTypeInfo<A, T> serializer, ArgumentTypeInfo.Template<A> properties, CallbackInfo ci) {
        Optional<ResourceKey<ArgumentTypeInfo<?, ?>>> entry = Registry.COMMAND_ARGUMENT_TYPE.getResourceKey(serializer);

        if (entry.isEmpty()) {
            return;
        }
        ResourceKey<ArgumentTypeInfo<?, ?>> keyed = entry.get();

        if (Arrays.asList(ignore).contains(keyed.location().toString())) {
            return;
        }
        ci.cancel();

        // Not a standard Minecraft argument type - so we need to wrap it
        serializeWrappedArgumentType(buf, serializer, properties);
    }

    private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void serializeWrappedArgumentType(FriendlyByteBuf packetByteBuf, ArgumentTypeInfo<A, T> serializer, ArgumentTypeInfo.Template<A> properties) {
        packetByteBuf.writeVarInt(MOD_ARGUMENT_INDICATOR);
        packetByteBuf.writeVarInt(Registry.COMMAND_ARGUMENT_TYPE.getId(serializer));

        FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
        serializer.serializeToNetwork((T) properties, extraData);

        packetByteBuf.writeVarInt(extraData.readableBytes());
        packetByteBuf.writeBytes(extraData);
    }
}

