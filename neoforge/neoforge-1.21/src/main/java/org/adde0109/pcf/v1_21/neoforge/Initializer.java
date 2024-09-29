package org.adde0109.pcf.v1_21.neoforge;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

import org.adde0109.pcf.PCF;

@SuppressWarnings("unused")
public class Initializer {
    public static void init() {
        PCF.resourceLocation = ResourceLocation::parse;
        PCF.component = Component::nullToEmpty;
        PCF.COMMAND_ARGUMENT_TYPE_KEY =
                (type) ->
                        BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getKey(
                                (ArgumentTypeInfo<?, ?>) type);
        PCF.COMMAND_ARGUMENT_TYPE_ID =
                (type) ->
                        BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getId(
                                (ArgumentTypeInfo<?, ?>) type);

        ModLoadingContext.get()
                .getActiveContainer()
                .registerConfig(
                        ModConfig.Type.COMMON,
                        org.adde0109.pcf.v1_20_2.neoforge.Initializer.configSpec);

        NeoForge.EVENT_BUS.addListener(
                org.adde0109.pcf.v1_20_2.neoforge.Initializer::serverAboutToStart);
    }
}
