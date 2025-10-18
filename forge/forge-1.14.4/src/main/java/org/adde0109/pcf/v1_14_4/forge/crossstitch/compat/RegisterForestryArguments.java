package org.adde0109.pcf.v1_14_4.forge.crossstitch.compat;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import forestry.apiculture.commands.CommandBeeGive.BeeArgument;
import forestry.arboriculture.commands.CommandTreeSpawn.TreeArugment;
import forestry.core.commands.CommandModules.CommandPluginsInfo.ModuleArgument;

import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_14_4.forge.crossstitch.ArgumentTypesUtil;

public final class RegisterForestryArguments {
    private static final ResourceLocation FORESTRY_BEE =
            new ResourceLocation("forestry:bee_argument");
    private static final ResourceLocation FORESTRY_MODULE =
            new ResourceLocation("forestry:module_argument");
    private static final ResourceLocation FORESTRY_TREE =
            new ResourceLocation("forestry:tree_argument");

    private static final boolean FORESTRY_14_16 =
            MetaAPI.instance().version().isInRange(MinecraftVersions.V14, MinecraftVersions.V16_5)
                    && MetaAPI.instance().isModLoaded("forestry");
    private static boolean FORESTRY_BEE_ARG_REGISTERED = false;
    private static boolean FORESTRY_MODULE_ARG_REGISTERED = false;
    private static boolean FORESTRY_TREE_ARG_REGISTERED = false;

    private RegisterForestryArguments() {}

    public static void applyFixes(ArgumentType<?> argumentType) {
        if (FORESTRY_14_16) {
            switch (argumentType) {
                case BeeArgument ignored -> {
                    if (!FORESTRY_BEE_ARG_REGISTERED
                            && !ArgumentTypesUtil.getByClassMap().containsKey(BeeArgument.class)) {
                        PCF.logger.debug(
                                "Injecting Forestry BeeArgument serializer into ArgumentTypes");
                        ArgumentTypes.register(
                                FORESTRY_BEE.toString(),
                                BeeArgument.class,
                                new EmptyArgumentSerializer<>(BeeArgument::beeArgument));
                        FORESTRY_BEE_ARG_REGISTERED = true;
                    }
                }
                case ModuleArgument ignored -> {
                    if (!FORESTRY_MODULE_ARG_REGISTERED
                            && !ArgumentTypesUtil.getByClassMap()
                                    .containsKey(ModuleArgument.class)) {
                        PCF.logger.debug(
                                "Injecting Forestry ModuleArgument serializer into ArgumentTypes");
                        ArgumentTypes.register(
                                FORESTRY_MODULE.toString(),
                                ModuleArgument.class,
                                new EmptyArgumentSerializer<>(ModuleArgument::modules));
                        FORESTRY_MODULE_ARG_REGISTERED = true;
                    }
                }
                case TreeArugment ignored -> {
                    if (!FORESTRY_TREE_ARG_REGISTERED
                            && !ArgumentTypesUtil.getByClassMap().containsKey(TreeArugment.class)) {
                        PCF.logger.debug(
                                "Injecting Forestry TreeArugment serializer into ArgumentTypes");
                        ArgumentTypes.register(
                                FORESTRY_TREE.toString(),
                                TreeArugment.class,
                                new EmptyArgumentSerializer<>(TreeArugment::treeArgument));
                        FORESTRY_TREE_ARG_REGISTERED = true;
                    }
                }
                case null, default -> {}
            }
        }
    }
}
