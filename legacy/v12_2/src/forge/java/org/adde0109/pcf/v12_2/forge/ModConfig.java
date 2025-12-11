package org.adde0109.pcf.v12_2.forge;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Configuration;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.forwarding.Mode;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("InstantiationOfUtilityClass")
@Config(modid = PCF.MOD_ID, name = "proxy-compatible-forge")
public final class ModConfig {
    @Comment("Config version, DO NOT CHANGE THIS")
    private static Double version = 2.0d;

    @Comment("Player Info Forwarding Settings")
    private static Forwarding forwarding = new Forwarding();

    @Comment("CrossStitch Settings - For Wrapping Modded Command Arguments")
    private static CrossStitch crossStitch = new CrossStitch();

    @Comment("Debug Settings")
    private static Debug debug = new Debug();

    private static final class Forwarding {
        @RequiresMcRestart
        @Comment(
                "Enable or disable player info forwarding. Changing this setting requires a server restart.")
        public static Boolean enabled = true;

        @Comment("The type of forwarding to use")
        public static Mode mode = Mode.MODERN;

        @Comment("The forwarding secret shared with the proxy")
        public static String secret = "";
    }

    private static final class CrossStitch {
        @RequiresMcRestart
        @Comment(
                "Enable or disable CrossStitch support. Changing this setting requires a server restart.")
        public static Boolean enabled = true;

        @Comment("Add any incompatible modded or vanilla command argument types here")
        public static List<String> forceWrappedArguments = List.of();

        @Comment(
                "Force wrap vanilla command argument types. Useful for when the above setting gets a bit excessive.")
        public static Boolean forceWrapVanillaArguments = false;
    }

    private static final class Debug {
        @Comment("Enable or disable debug mode.")
        public static Boolean enabled = false;

        @Comment(
                "List of mixins to disable. Use the Mixin's name and prefix it with it's partial or full package name.")
        public static List<String> disabledMixins = List.of();
    }

    public static void reload() {
        Path configDir = Path.of("config");
        File file = configDir.resolve("proxy-compatible-forge.cfg").toFile();
        Configuration config = new Configuration(file);
        try {
            config.load();
        } catch (Exception e) {
            PCF.logger.error("Could not load config file!", e);
        } finally {
            config.save();
        }
        ModConfig.version = config.get("general", "version", 2.0d).getDouble(2.0d);

        Forwarding.enabled =
                config.getBoolean(
                        "enabled",
                        "forwarding",
                        true,
                        "Enable or disable player info forwarding. Changing this setting requires a server restart.");
        String modeStr =
                config.getString(
                        "mode", "forwarding", Mode.MODERN.name(), "The type of forwarding to use");
        try {
            Forwarding.mode = Mode.valueOf(modeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            PCF.logger.error(
                    "Invalid forwarding mode in config: "
                            + modeStr
                            + ". Using default: "
                            + Mode.MODERN.name());
        }
        Forwarding.secret =
                config.getString(
                        "secret", "forwarding", "", "The forwarding secret shared with the proxy");

        CrossStitch.enabled =
                config.getBoolean(
                        "enabled",
                        "crossstitch",
                        true,
                        "Enable or disable CrossStitch support. Changing this setting requires a server restart.");
        CrossStitch.forceWrappedArguments =
                List.of(
                        config.getStringList(
                                "forceWrappedArguments",
                                "crossstitch",
                                new String[0],
                                "Add any incompatible modded or vanilla command argument types here"));
        CrossStitch.forceWrapVanillaArguments =
                config.getBoolean(
                        "forceWrapVanillaArguments",
                        "crossstitch",
                        false,
                        "Force wrap vanilla command argument types. Useful for when the above setting gets a bit excessive.");

        Debug.enabled =
                config.getBoolean("enabled", "debug", false, "Enable or disable debug mode.");
        Debug.disabledMixins =
                List.of(
                        config.getStringList(
                                "disabledMixins",
                                "debug",
                                new String[0],
                                "List of mixins to disable. Use the Mixin's name and prefix it with it's partial or full package name."));

        config.save();

        String forwardingSecret = Forwarding.secret;
        PCF.instance()
                .setForwarding(
                        new PCF.Forwarding(Forwarding.enabled, Forwarding.mode, forwardingSecret));
        PCF.instance()
                .setCrossStitch(
                        new PCF.CrossStitch(
                                CrossStitch.enabled,
                                CrossStitch.forceWrappedArguments,
                                CrossStitch.forceWrapVanillaArguments));
        PCF.instance().setDebug(new PCF.Debug(Debug.enabled, Debug.disabledMixins));
    }
}
