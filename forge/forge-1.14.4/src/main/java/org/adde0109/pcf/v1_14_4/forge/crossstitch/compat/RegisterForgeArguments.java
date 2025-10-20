package org.adde0109.pcf.v1_14_4.forge.crossstitch.compat;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_14_4.forge.crossstitch.ArgumentTypesUtil;

public final class RegisterForgeArguments {
    private static final String FORGE_ENUM = "forge:enum";
    private static final String FORGE_MODID = "forge:modid";
    private static final boolean IS_FORGE_14_15 =
            MetaAPI.instance().platform().isForge()
                    && MetaAPI.instance()
                            .version()
                            .isInRange(MinecraftVersions.V14, MinecraftVersions.V15_2);

    private RegisterForgeArguments() {}

    @SuppressWarnings("unchecked")
    public static void applyFix() {
        if (IS_FORGE_14_15) {
            if (!ArgumentTypesUtil.getByClassMap().containsKey(ModIdArgument.class)) {
                PCF.logger.debug(
                        "Injecting Dummy Forge ModIdArgument serializer into ArgumentTypes");
                ArgumentTypes.register(
                        FORGE_MODID,
                        ModIdArgument.class,
                        new EmptyArgumentSerializer<>(ModIdArgument::new));
            }
            if (!ArgumentTypesUtil.getByClassMap().containsKey(EnumArgument.class)) {
                PCF.logger.debug(
                        "Injecting Dummy Forge EnumArgument serializer into ArgumentTypes");
                ArgumentTypes.register(
                        FORGE_ENUM,
                        EnumArgument.class,
                        new EmptyArgumentSerializer<>(() -> EnumArgument.enumArgument(Enum.class)));
            }
        }
    }
}
