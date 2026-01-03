package org.adde0109.pcf.v14_4.forge;

import static org.adde0109.pcf.PCF.CONFIG_FILE_NAME;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.forwarding.Mode;
import org.adde0109.pcf.forwarding.modern.VelocityProxy;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ApiStatus.Internal
public final class Config {
    private static final Config config;
    public static final ForgeConfigSpec spec;

    static {
        final Pair<Config, ForgeConfigSpec> specPair =
                new ForgeConfigSpec.Builder().configure(Config::new);
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

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final ForgeConfigSpec.ConfigValue<Double> version;

    private final ForgeConfigSpec.ConfigValue<Boolean> enableForwarding;
    private final ForgeConfigSpec.ConfigValue<Mode> forwardingMode;
    private final ForgeConfigSpec.ConfigValue<String> forwardingSecret;

    private final ForgeConfigSpec.ConfigValue<Boolean> enableCrossStitch;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> forceWrappedArguments;
    private final ForgeConfigSpec.ConfigValue<Boolean> forceWrapVanillaArguments;

    private final ForgeConfigSpec.ConfigValue<Boolean> enableDebug;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> disabledMixins;

    private final ForgeConfigSpec.ConfigValue<VelocityProxy.Version> modernForwardingVersion;

    Config(ForgeConfigSpec.Builder builder) {
        version = builder.comment("Config version, DO NOT CHANGE THIS").define("version", 2.0d);

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

        builder.comment("Advanced Settings").push("advanced");
        modernForwardingVersion =
                builder.comment("Overrides the modern forwarding version decided by PCF.")
                        .defineEnum("modernForwardingVersion", VelocityProxy.Version.NO_OVERRIDE);
        builder.pop();
    }

    @SuppressWarnings("unchecked")
    public static void reload() {
        String forwardingSecret = Config.config.forwardingSecret.get();
        PCF.instance()
                .setForwarding(
                        new PCF.Forwarding(
                                Config.config.enableForwarding.get(),
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
        PCF.instance().setAdvanced(new PCF.Advanced(Config.config.modernForwardingVersion.get()));
    }
}
