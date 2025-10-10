package org.adde0109.pcf.v1_14_4.forge.crossstitch.compat;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_14_4.forge.crossstitch.ArgumentTypesUtil;
import org.adde0109.pcf.v1_14_4.forge.crossstitch.DummyEnumArgumentSupplier;

public final class RegisterForgeArguments {
    private static final ResourceLocation FORGE_ENUM = new ResourceLocation("forge:enum");
    private static final ResourceLocation FORGE_MODID = new ResourceLocation("forge:modid");

    private RegisterForgeArguments() {}

    public static void applyFixes(ArgumentType<?> argumentType) {
        if (IS_FORGE_14_15) {
            if (argumentType.getClass() == EnumArgument.class) {
                if (!FORGE_MODID_ARG_REGISTERED
                        && ArgumentTypesUtil.getByClassMap().containsKey(ModIdArgument.class)) {
                    FORGE_MODID_ARG_REGISTERED = true;
                }
                registerForgeEnumArgument();
            } else if (argumentType.getClass() == ModIdArgument.class) {
                if (!FORGE_ENUM_ARG_REGISTERED
                        && ArgumentTypesUtil.getByClassMap().containsKey(EnumArgument.class)) {
                    FORGE_ENUM_ARG_REGISTERED = true;
                }
                registerForgeModIdArgument();
            }
        }
    }

    private static final boolean IS_FORGE_14_15 =
            MetaAPI.instance().platform().isForge()
                    && MetaAPI.instance()
                            .version()
                            .isInRange(MinecraftVersions.V14, MinecraftVersions.V15_2);
    private static boolean FORGE_ENUM_ARG_REGISTERED = false;
    private static boolean FORGE_MODID_ARG_REGISTERED = false;

    private static void registerForgeModIdArgument() {
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
