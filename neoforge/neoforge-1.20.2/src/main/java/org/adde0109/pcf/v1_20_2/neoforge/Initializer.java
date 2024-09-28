package org.adde0109.pcf.v1_20_2.neoforge;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import org.adde0109.pcf.common.CommonInitializer;
import org.adde0109.pcf.common.ModernForwarding;
import org.apache.commons.lang3.tuple.Pair;

@SuppressWarnings("unused")
public class Initializer {
    private static final Config config;

    public static void init() {
        CommonInitializer.resourceLocation = ResourceLocation::new;
        CommonInitializer.COMMAND_ARGUMENT_TYPE_KEY = (type) -> BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getKey((ArgumentTypeInfo<?, ?>) type);
        CommonInitializer.COMMAND_ARGUMENT_TYPE_ID = (type) -> BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getId((ArgumentTypeInfo<?, ?>) type);
        CommonInitializer.setupIntegratedArgumentTypes();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configSpec);

        NeoForge.EVENT_BUS.addListener(Initializer::serverAboutToStart);
    }

    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        String forwardingSecret = config.forwardingSecret.get();
        if(!(forwardingSecret.isBlank() || forwardingSecret.isEmpty())) {
            CommonInitializer.modernForwarding = new ModernForwarding(forwardingSecret);
        }
    }

    public static final ModConfigSpec configSpec;
    static {
        final Pair<Config, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Config::new);
        configSpec = specPair.getRight();
        config = specPair.getLeft();
    }

    public static class Config {
        public final ModConfigSpec.ConfigValue<? extends String> forwardingSecret;

        Config(ModConfigSpec.Builder builder) {
            builder.comment("Modern Forwarding Settings")
                    .push("modernForwarding");

            forwardingSecret = builder
                    .define("forwardingSecret", "");

            builder.pop();
        }
    }
}
