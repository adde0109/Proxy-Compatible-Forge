//Code modified from https://github.com/VelocityPowered/CrossStitch

package org.adde0109.ambassador.forge.mixin.command;

import com.mojang.brigadier.arguments.ArgumentType;
import io.netty.buffer.Unpooled;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;

@Mixin(SCommandListPacket.class)
public class CommandTreeSerializationMixin {
    private static final ResourceLocation MOD_ARGUMENT_INDICATOR = new ResourceLocation("crossstitch:mod_argument");

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
            "minecraft:angle" // added in 1.16.2
    };

    @Redirect(method = "writeNode", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/arguments/ArgumentTypes;serialize(Lnet/minecraft/network/PacketBuffer;Lcom/mojang/brigadier/arguments/ArgumentType;)V"))
    private static void writeNode$wrapInVelocityModArgument(PacketBuffer packetByteBuf, ArgumentType<?> type) {
        ArgumentTypes.Entry entry = ArgumentTypes.get(type);
        if (entry == null) {
            packetByteBuf.writeResourceLocation(new ResourceLocation(""));
            return;
        }
        if (Arrays.stream(ignore).anyMatch(entry.name.toString()::equals)) {
            packetByteBuf.writeResourceLocation(entry.name);
            entry.serializer.serializeToNetwork(type, packetByteBuf);
            return;
        }

        // Not a standard Minecraft argument type - so we need to wrap it
        serializeWrappedArgumentType(packetByteBuf, type, entry);
    }

    private static void serializeWrappedArgumentType(PacketBuffer packetByteBuf, ArgumentType argumentType, ArgumentTypes.Entry entry) {
        packetByteBuf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);

        packetByteBuf.writeResourceLocation(entry.name);

        PacketBuffer extraData = new PacketBuffer(Unpooled.buffer());
        entry.serializer.serializeToNetwork(argumentType, extraData);

        packetByteBuf.writeVarInt(extraData.readableBytes());
        packetByteBuf.writeBytes(extraData);
    }
}

