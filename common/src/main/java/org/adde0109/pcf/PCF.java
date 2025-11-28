package org.adde0109.pcf;

import dev.neuralnexus.taterapi.loader.EntrypointLoader;
import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.ModResource;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;

import org.adde0109.pcf.forwarding.Mode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;

public final class PCF {
    private PCF() {}

    public static final String MOD_ID = "pcf";
    public static final String CONFIG_FILE_NAME = "proxy-compatible-forge.toml";

    private static final PCF INSTANCE = new PCF();
    public static final Logger logger = Logger.create(MOD_ID);

    public static PCF instance() {
        return INSTANCE;
    }

    private static final @NotNull String SERVICE_PATH =
            "META-INF/services/org.adde0109.pcf.PCFInitializer";
    private static EntrypointLoader<PCFInitializer> loader;

    @ApiStatus.Internal
    void onInit() {
        MetaAPI api = MetaAPI.instance();
        MinecraftVersion mcv = api.version();
        Platform platform = api.platform();

        // spotless:off
        PCF.logger.info("Initializing Proxy Compatible Forge on "
                + "Minecraft " + mcv
                + " (" + platform + " " + api.meta().apiVersion() + ")");
        // spotless:on

        final boolean debug = Constraint.Evaluator.DEBUG;
        Constraint.Evaluator.DEBUG = this.debug().enabled();

        final ModContainer<?> container = MetaAPI.instance().mod(MOD_ID).orElseThrow();
        try (final ModResource resource = container.resource()) {
            final Path servicePath = resource.getResourceOrThrow(SERVICE_PATH);
            loader =
                    EntrypointLoader.builder()
                            .entrypointClass(PCFInitializer.class)
                            .logger(logger)
                            .servicePaths(servicePath)
                            .useServiceLoader(false)
                            .useOtherProviders(true)
                            .build();

            loader.load();
        } catch (final Exception e) {
            PCF.logger.error("Failed to access PCF Mod Resources", e);
        }
        loader.onInit();

        Constraint.Evaluator.DEBUG = debug;
    }

    @ApiStatus.Internal
    public static void forceLoadConfig() {
        try {
            if (MetaAPI.instance().isPlatformPresent(Platforms.FORGE)) {
                Class.forName("org.adde0109.pcf.v1_14_4.forge.Config")
                        .getMethod("reload")
                        .invoke(null);
            } else if (MetaAPI.instance().isPlatformPresent(Platforms.NEOFORGE)) {
                Class.forName("org.adde0109.pcf.v1_20_2.neoforge.Config")
                        .getMethod("reload")
                        .invoke(null);
            }
        } catch (ClassNotFoundException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            logger.error("Failed to load Config class", e);
        }
    }

    private Forwarding forwarding;

    public Forwarding forwarding() {
        return this.forwarding;
    }

    @ApiStatus.Internal
    public void setForwarding(final @NotNull Forwarding forwarding) {
        this.forwarding = forwarding;
    }

    private CrossStitch crossStitch;

    public CrossStitch crossStitch() {
        return this.crossStitch;
    }

    @ApiStatus.Internal
    public void setCrossStitch(final @NotNull CrossStitch crossStitch) {
        this.crossStitch = crossStitch;
    }

    private Debug debug;

    public Debug debug() {
        return this.debug;
    }

    @ApiStatus.Internal
    public void setDebug(final @NotNull Debug debug) {
        this.debug = debug;
    }

    public record Forwarding(boolean enabled, Mode mode, String secret) {}

    public record CrossStitch(
            boolean enabled,
            List<String> forceWrappedArguments,
            boolean forceWrapVanillaArguments) {}

    public record Debug(boolean enabled, List<String> disabledMixins) {}
}
