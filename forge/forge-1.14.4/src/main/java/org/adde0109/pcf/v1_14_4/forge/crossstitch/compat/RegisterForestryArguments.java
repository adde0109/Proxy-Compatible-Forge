package org.adde0109.pcf.v1_14_4.forge.crossstitch.compat;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import forestry.apiculture.commands.CommandBeeGive.BeeArgument;
import forestry.arboriculture.commands.CommandTreeSpawn.TreeArugment;
import forestry.core.commands.CommandModules.CommandPluginsInfo.ModuleArgument;

import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_14_4.forge.crossstitch.ArgumentTypesUtil;

public final class RegisterForestryArguments {
    private static final String FORESTRY_BEE = "forestry:bee_argument";
    private static final String FORESTRY_MODULE = "forestry:module_argument";
    private static final String FORESTRY_TREE = "forestry:tree_argument";
    private static final boolean FORESTRY_14_16 =
            MetaAPI.instance().version().isInRange(MinecraftVersions.V14, MinecraftVersions.V16_5)
                    && MetaAPI.instance().isModLoaded("forestry");

    private RegisterForestryArguments() {}

    public static void applyFix() {
        if (FORESTRY_14_16) {
            if (!ArgumentTypesUtil.getByClassMap().containsKey(BeeArgument.class)) {
                PCF.logger.debug("Injecting Forestry BeeArgument serializer into ArgumentTypes");
                ArgumentTypes.register(
                        FORESTRY_BEE,
                        BeeArgument.class,
                        new EmptyArgumentSerializer<>(BeeArgument::beeArgument));
            }
            if (!ArgumentTypesUtil.getByClassMap().containsKey(ModuleArgument.class)) {
                PCF.logger.debug("Injecting Forestry ModuleArgument serializer into ArgumentTypes");
                ArgumentTypes.register(
                        FORESTRY_MODULE,
                        ModuleArgument.class,
                        new EmptyArgumentSerializer<>(ModuleArgument::modules));
            }
            if (!ArgumentTypesUtil.getByClassMap().containsKey(TreeArugment.class)) {
                PCF.logger.debug("Injecting Forestry TreeArugment serializer into ArgumentTypes");
                ArgumentTypes.register(
                        FORESTRY_TREE,
                        TreeArugment.class,
                        new EmptyArgumentSerializer<>(TreeArugment::treeArgument));
            }
        }
    }
}
