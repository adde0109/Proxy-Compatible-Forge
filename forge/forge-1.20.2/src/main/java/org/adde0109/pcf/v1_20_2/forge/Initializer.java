package org.adde0109.pcf.v1_20_2.forge;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import org.adde0109.pcf.common.CommonInitializer;
import org.adde0109.pcf.common.ModernForwarding;
import org.apache.commons.lang3.tuple.Pair;

public class Initializer {
    public static final Config config;

    public static void init() {
        CommonInitializer.resourceLocation = ResourceLocation::new;
        CommonInitializer.COMMAND_ARGUMENT_TYPE = (type) -> ForgeRegistries.COMMAND_ARGUMENT_TYPES.getKey((ArgumentTypeInfo<?, ?>) type);
        CommonInitializer.setupIntegratedArgumentTypes();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,configSpec);

        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));

        MinecraftForge.EVENT_BUS.addListener(Initializer::serverAboutToStart);
    }

    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        String forwardingSecret = config.forwardingSecret.get();
        if(!(forwardingSecret.isBlank() || forwardingSecret.isEmpty())) {
            CommonInitializer.modernForwarding = new ModernForwarding(forwardingSecret);
        }
    }

    static final ForgeConfigSpec configSpec;
    static {
        final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Initializer.Config::new);
        configSpec = specPair.getRight();
        config = specPair.getLeft();
    }

    public static class Config {
        public final ForgeConfigSpec.ConfigValue<? extends String> forwardingSecret;

        Config(ForgeConfigSpec.Builder builder) {
          builder.comment("Modern Forwarding Settings")
                  .push("modernForwarding");

          forwardingSecret = builder
                  .define("forwardingSecret", "");

          builder.pop();
        }
    }
}
