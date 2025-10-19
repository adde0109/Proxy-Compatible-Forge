package org.adde0109.pcf.v1_20_2.neoforge;

import static org.adde0109.pcf.PCF.CONFIG_FILE_NAME;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.forwarding.Mode;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@ApiStatus.Internal
public final class Config {
    private static final Config config;
    public static final ModConfigSpec spec;

    static {
        final Pair<Config, ModConfigSpec> specPair =
                new ModConfigSpec.Builder().configure(Config::new);
        spec = specPair.getRight();
        config = specPair.getLeft();

        final CommentedFileConfig config =
                CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE_NAME))
                        .sync()
                        .preserveInsertionOrder()
                        .onFileNotFound(
                                (file, configFormat) -> {
                                    Files.createDirectories(file.getParent());
                                    Path p = FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE_NAME);
                                    if (Files.exists(p)) {
                                        Files.copy(p, file);
                                    } else {
                                        Files.createFile(file);
                                        configFormat.initEmptyFile(file);
                                    }
                                    return true;
                                })
                        .autosave()
                        .writingMode(WritingMode.REPLACE)
                        .build();
        config.load();
        try { // Fix due to the return type of ModConfigSpec#correct being changed from int to void
            ModConfigSpec.class.getMethod("correct", CommentedConfig.class).invoke(spec, config);
            if (MetaAPI.instance().version().isAtLeast(MinecraftVersions.V21)) {
                // This is unholy, maybe we should just parse the toml and grab the values manually
                // TODO: Maybe swap to method handles
                // spotless:off
                //noinspection JavaReflectionMemberAccess
                Constructor<ModConfig> modConfigConstructor =
                        ModConfig.class.getDeclaredConstructor(
                                ModConfig.Type.class,
                                IConfigSpec.class,
                                ModContainer.class,
                                String.class,
                                ReentrantLock.class);
                modConfigConstructor.setAccessible(true);
                ModConfig dummyModConfig =
                        modConfigConstructor.newInstance(
                                ModConfig.Type.COMMON,
                                spec,
                                null,
                                CONFIG_FILE_NAME,
                                new ReentrantLock());

                Class<?> loadedConfigClass = Class.forName("net.neoforged.fml.config.LoadedConfig");
                Constructor<?> constructor =
                        loadedConfigClass.getDeclaredConstructor(CommentedConfig.class, Path.class, ModConfig.class);
                constructor.setAccessible(true);

                Object loadedConfig = constructor.newInstance(config, config.getNioPath(), dummyModConfig);
                Class<?> loadedConfigInterface = IConfigSpec.class.getDeclaredClasses()[0];
                ModConfigSpec.class
                        .getMethod("acceptConfig", loadedConfigInterface)
                        .invoke(spec, loadedConfig);
                // spotless:on
            } else {
                ModConfigSpec.class
                        .getMethod("acceptConfig", CommentedConfig.class)
                        .invoke(spec, config);
            }
        } catch (ClassNotFoundException
                | IllegalAccessException
                | InstantiationException
                | InvocationTargetException
                | NoSuchMethodException e) {
            PCF.logger.error("Failed to load config file", e);
        }
    }

    private final ModConfigSpec.ConfigValue<Boolean> enableForwarding;
    private final ModConfigSpec.ConfigValue<Mode> forwardingMode;
    private final ModConfigSpec.ConfigValue<String> forwardingSecret;

    private final ModConfigSpec.ConfigValue<Boolean> enableCrossStitch;
    private final ModConfigSpec.ConfigValue<List<? extends String>> forceWrappedArguments;
    private final ModConfigSpec.ConfigValue<Boolean> forceWrapVanillaArguments;

    private final ModConfigSpec.ConfigValue<Boolean> enableDebug;
    private final ModConfigSpec.ConfigValue<List<? extends String>> disabledMixins;

    Config(ModConfigSpec.Builder builder) {
        builder.comment("Player Info Forwarding Settings").push("forwarding");
        enableForwarding =
                builder.comment(
                                "Enable or disable player info forwarding. Changing this setting requires a server restart.")
                        .define("enabled", true);
        forwardingMode =
                builder.comment("The type of forwarding to use").defineEnum("mode", Mode.MODERN);
        forwardingSecret =
                builder.comment("The forwarding secret shared with the proxy").define("secret", "");
        builder.pop();

        builder.comment("CrossStitch Settings - For Wrapping Modded Command Arguments")
                .push("crossStitch");
        enableCrossStitch =
                builder.comment(
                                "Enable or disable CrossStitch support. Changing this setting requires a server restart.")
                        .define("enabled", true);
        forceWrappedArguments =
                builder.comment(
                                "Add any incompatible modded or vanilla command argument types here")
                        .defineList("forceWrappedArguments", List.of(), (obj) -> true);
        forceWrapVanillaArguments =
                builder.comment(
                                "Force wrap vanilla command argument types. Useful for when the above setting gets a bit excessive.")
                        .define("forceWrapVanillaArguments", false);
        builder.pop();

        builder.comment("Debug Settings").push("debug");
        enableDebug = builder.comment("Enable or disable debug mode.").define("enabled", false);
        disabledMixins =
                builder.comment(
                                "List of mixins to disable. Use the Mixin's name and prefix it with it's partial or full package name.")
                        .defineList("disabledMixins", List.of(), (obj) -> true);
        builder.pop();
    }

    @SuppressWarnings("unchecked")
    public static void reload() {
        String forwardingSecret = Config.config.forwardingSecret.get();
        PCF.instance()
                .setForwarding(
                        new PCF.Forwarding(
                                Config.config.enableForwarding.get() && !forwardingSecret.isBlank(),
                                Config.config.forwardingMode.get(),
                                forwardingSecret));
        PCF.instance()
                .setCrossStitch(
                        new PCF.CrossStitch(
                                Config.config.enableCrossStitch.get(),
                                (List<String>) Config.config.forceWrappedArguments.get(),
                                Config.config.forceWrapVanillaArguments.get()));
        PCF.instance()
                .setDebug(
                        new PCF.Debug(
                                Config.config.enableDebug.get(),
                                (List<String>) Config.config.disabledMixins.get()));
    }
}
