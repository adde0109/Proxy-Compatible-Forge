package org.adde0109.pcf.v1_14_4.forge.crossstitch;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import io.netty.buffer.Unpooled;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_14_4.forge.reflection.ArgumentTypesUtil;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Adapted from <a
 * href="https://github.com/VelocityPowered/CrossStitch/blob/fe3f3be49c52dc9c1b6b7cd3cafefb953adf4486/src/main/java/com/velocitypowered/crossstitch/mixin/command/CommandTreeSerializationMixin.java">CrossStitch</a>
 */
public final class CrossStitchUtil {
    private static final ResourceLocation MOD_ARGUMENT_INDICATOR =
            new ResourceLocation("crossstitch:mod_argument");
    private static final ResourceLocation FORGE_ENUM = new ResourceLocation("forge:enum");
    private static final ResourceLocation FORGE_MODID = new ResourceLocation("forge:modid");

    public static void writeNode$wrapInVelocityModArgument(
            FriendlyByteBuf buf,
            CommandNode<SharedSuggestionProvider> node,
            Map<CommandNode<SharedSuggestionProvider>, Integer> ignored,
            CallbackInfo ci) {
        ArgumentCommandNode<SharedSuggestionProvider, ?> argNode =
                (ArgumentCommandNode<SharedSuggestionProvider, ?>) node;
        ArgumentType<?> argumentType = argNode.getType();

        // Forge didn't implement arg serializers on some versions
        if (IS_FORGE_14_15) {
            if (argumentType.getClass() == EnumArgument.class) {
                registerForgeEnumArgument();
            } else if (argumentType.getClass() == ModIdArgument.class) {
                registerForgeModIdArgument();
            }
        }

        Object entry = ArgumentTypesUtil.getEntry(argumentType);
        if (entry == null) {
            PCF.logger.debug(
                    "ArgumentTypes has no entry for type: " + argumentType.getClass().getName());
            return;
        }
        ResourceLocation identifier = ArgumentTypesUtil.getName(entry);
        if (PCF.isIntegratedArgument(identifier.toString())) {
            return;
        }

        // Not a standard Minecraft argument type - so we need to wrap it
        PCF.logger.debug("Wrapping argument with identifier: " + identifier);
        serializeWrappedArgumentType(buf, argumentType, entry);
        ci.cancel();
    }

    private static void serializeWrappedArgumentType(
            FriendlyByteBuf buf, ArgumentType<?> argumentType, Object entry) {
        buf.writeResourceLocation(MOD_ARGUMENT_INDICATOR);

        buf.writeResourceLocation(ArgumentTypesUtil.getName(entry));

        FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
        ArgumentTypesUtil.getSerializer(entry).serializeToNetwork(argumentType, extraData);

        buf.writeVarInt(extraData.readableBytes());
        buf.writeBytes(extraData);
    }

    // Forge-specific fixes
    private static final boolean IS_FORGE_14_15 =
            MetaAPI.instance().platform().isForge()
                    && MetaAPI.instance()
                            .version()
                            .isInRange(MinecraftVersions.V14, MinecraftVersions.V15_2);
    private static boolean FORGE_ENUM_ARG_REGISTERED = false;
    private static boolean FORGE_MODID_ARG_REGISTERED = false;

    private static void registerForgeModIdArgument() {
        if (!FORGE_MODID_ARG_REGISTERED
                && ArgumentTypesUtil.getByClassMap().containsKey(ModIdArgument.class)) {
            FORGE_MODID_ARG_REGISTERED = true;
        }
        if (!FORGE_MODID_ARG_REGISTERED) {
            PCF.logger.debug("Injecting ModIdArgument serializer into ArgumentTypes");
            ArgumentTypes.register(
                    FORGE_MODID.toString(),
                    ModIdArgument.class,
                    new EmptyArgumentSerializer<>(ModIdArgument::new));
            FORGE_MODID_ARG_REGISTERED = true;
        }
    }

    private static void registerForgeEnumArgument() {
        if (!FORGE_ENUM_ARG_REGISTERED
                && ArgumentTypesUtil.getByClassMap().containsKey(EnumArgument.class)) {
            FORGE_ENUM_ARG_REGISTERED = true;
        }
        if (!FORGE_ENUM_ARG_REGISTERED) {
            PCF.logger.debug("Injecting EnumArgument serializer into ArgumentTypes");
            ArgumentTypes.register(
                    FORGE_ENUM.toString(),
                    EnumArgument.class,
                    new EmptyArgumentSerializer<>(new DummyEnumArgumentSupplier()));
            FORGE_ENUM_ARG_REGISTERED = true;
        }
    }
}
