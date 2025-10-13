package org.adde0109.pcf.v1_14_4.forge;

import net.minecraftforge.common.ForgeConfigSpec;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.forwarding.Mode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public final class Config {
    public static final Config config;
    public static final ForgeConfigSpec spec;

    static {
        final Pair<Config, ForgeConfigSpec> specPair =
                new ForgeConfigSpec.Builder().configure(Config::new);
        spec = specPair.getRight();
        config = specPair.getLeft();
    }

    private final ForgeConfigSpec.ConfigValue<Boolean> enableForwarding;
    private final ForgeConfigSpec.ConfigValue<Mode> forwardingMode;
    private final ForgeConfigSpec.ConfigValue<String> forwardingSecret;
    private final ForgeConfigSpec.ConfigValue<Boolean> enableCrossStitch;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> forceWrappedArguments;

    Config(ForgeConfigSpec.Builder builder) {
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

        builder.comment("CrossStitch Settings").push("crossStitch");
        enableCrossStitch =
                builder.comment(
                                "Enable or disable CrossStitch support. This setting requires a server restart")
                        .define("enabled", true);
        forceWrappedArguments =
                builder.comment(
                                "Add any incompatible modded or vanilla command argument types here")
                        .defineList("forceWrappedArguments", List.of(), (obj) -> true);
        builder.pop();
    }

    @SuppressWarnings("unchecked")
    public static void setupConfig() {
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
    }
}
