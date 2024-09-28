package org.adde0109.pcf.v1_18.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.ModernForwarding;
import org.apache.commons.lang3.tuple.Pair;

@SuppressWarnings("unused")
public class Initializer {
    public static final Config config;

    public static void init() {
        PCF.resourceLocation = ResourceLocation::new;

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configSpec);

        ModLoadingContext.get()
                .registerExtensionPoint(
                        IExtensionPoint.DisplayTest.class,
                        () ->
                                new IExtensionPoint.DisplayTest(
                                        () -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        MinecraftForge.EVENT_BUS.addListener(Initializer::serverAboutToStart);
    }

    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        String forwardingSecret = config.forwardingSecret.get();
        if (!(forwardingSecret.isBlank() || forwardingSecret.isEmpty())) {
            PCF.modernForwarding = new ModernForwarding(forwardingSecret);
        }
    }

    static final ForgeConfigSpec configSpec;

    static {
        final Pair<Config, ForgeConfigSpec> specPair =
                new ForgeConfigSpec.Builder().configure(Config::new);
        configSpec = specPair.getRight();
        config = specPair.getLeft();
    }

    public static class Config {
        public final ForgeConfigSpec.ConfigValue<? extends String> forwardingSecret;

        Config(ForgeConfigSpec.Builder builder) {
            builder.comment("Modern Forwarding Settings").push("modernForwarding");

            forwardingSecret = builder.define("forwardingSecret", "");

            builder.pop();
        }
    }
}
