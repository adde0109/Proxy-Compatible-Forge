package org.adde0109.pcf.v1_20_2.neoforge;

import net.neoforged.neoforge.common.ModConfigSpec;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.forwarding.Mode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class Config {
    public static final Config config;
    public static final ModConfigSpec spec;

    static {
        final Pair<Config, ModConfigSpec> specPair =
                new ModConfigSpec.Builder().configure(Config::new);
        spec = specPair.getRight();
        config = specPair.getLeft();
    }

    private final ModConfigSpec.ConfigValue<Boolean> enableForwarding;
    private final ModConfigSpec.ConfigValue<Mode> forwardingMode;
    private final ModConfigSpec.ConfigValue<String> forwardingSecret;
    private final ModConfigSpec.ConfigValue<Boolean> enableCrossStitch;
    private final ModConfigSpec.ConfigValue<List<? extends String>> forceWrappedArguments;

    Config(ModConfigSpec.Builder builder) {
        builder.comment("Player Info Forwarding Settings").push("forwarding");

        enableForwarding = builder
                .comment("Enable or disable player info forwarding")
                .define("enabled", true);
        builder.pop();

        forwardingMode = builder
                .comment("The type of forwarding to use. Currently, only 'modern' is supported")
                .defineEnum("mode", Mode.MODERN);
        builder.pop();

        forwardingSecret = builder
                .comment("The forwarding secret shared with the proxy")
                .define("secret", "");
        builder.pop();

        builder.comment("CrossStitch Settings").push("crossStitch");

        enableCrossStitch = builder
                .comment("Enable or disable CrossStitch support")
                .define("enabled", true);
        builder.pop();

        forceWrappedArguments =
                builder.comment("Add any incompatible modded or vanilla command argument types here")
                        .defineList("forceWrappedArguments", List.of(), (obj) -> true);
        builder.pop();
    }

    @SuppressWarnings("unchecked")
    public static void setupConfig() {
        String forwardingSecret = Config.config.forwardingSecret.get();
        boolean enableForwarding = Config.config.enableForwarding.get() && !forwardingSecret.isBlank();
        Mode forwardingMode = Config.config.forwardingMode.get();
        PCF.instance().setForwarding(new PCF.Forwarding(enableForwarding, forwardingMode, forwardingSecret));

        boolean enableCrossStitch = Config.config.enableCrossStitch.get();
        List<String> forceWrappedArguments = (List<String>) Config.config.forceWrappedArguments.get();
        PCF.instance().setCrossStitch(new PCF.CrossStitch(enableCrossStitch, forceWrappedArguments));
    }
}
