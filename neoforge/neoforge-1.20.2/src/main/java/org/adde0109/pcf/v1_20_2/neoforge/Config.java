package org.adde0109.pcf.v1_20_2.neoforge;

import static org.adde0109.pcf.PCF.CONFIG_FILE_NAME;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.forwarding.Mode;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
        spec.correct(config);
        spec.setConfig(config);
    }

    private final ModConfigSpec.ConfigValue<Boolean> enableForwarding;
    private final ModConfigSpec.ConfigValue<Mode> forwardingMode;
    private final ModConfigSpec.ConfigValue<String> forwardingSecret;

    private final ModConfigSpec.ConfigValue<Boolean> enableCrossStitch;
    private final ModConfigSpec.ConfigValue<List<? extends String>> forceWrappedArguments;

    private final ModConfigSpec.ConfigValue<Boolean> enableDebug;
    private final ModConfigSpec.ConfigValue<List<? extends String>> disabledMixins;

    Config(ModConfigSpec.Builder builder) {
        builder.comment("Player Info Forwarding Settings").push("forwarding");
        enableForwarding =
                builder.comment(
                                "Enable or disable player info forwarding. This setting requires a server restart")
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
                                "Enable or disable CrossStitch support. This setting requires a server restart")
                        .define("enabled", true);
        forceWrappedArguments =
                builder.comment(
                                "Add any incompatible modded or vanilla command argument types here")
                        .defineList("forceWrappedArguments", List.of(), (obj) -> true);
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
        boolean enableForwarding =
                Config.config.enableForwarding.get() && !forwardingSecret.isBlank();
        Mode forwardingMode = Config.config.forwardingMode.get();
        PCF.instance()
                .setForwarding(
                        new PCF.Forwarding(enableForwarding, forwardingMode, forwardingSecret));

        boolean enableCrossStitch = Config.config.enableCrossStitch.get();
        List<String> forceWrappedArguments =
                (List<String>) Config.config.forceWrappedArguments.get();
        PCF.instance()
                .setCrossStitch(new PCF.CrossStitch(enableCrossStitch, forceWrappedArguments));

        boolean enableDebug = Config.config.enableDebug.get();
        List<String> disabledMixins = (List<String>) Config.config.disabledMixins.get();
        PCF.instance().setDebug(new PCF.Debug(enableDebug, disabledMixins));
    }
}
