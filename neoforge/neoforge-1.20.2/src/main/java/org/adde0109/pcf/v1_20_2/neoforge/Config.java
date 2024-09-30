package org.adde0109.pcf.v1_20_2.neoforge;

import net.neoforged.neoforge.common.ModConfigSpec;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.ModernForwarding;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    public static final Config config;
    public static final ModConfigSpec spec;

    static {
        final Pair<Config, ModConfigSpec> specPair =
                new ModConfigSpec.Builder().configure(Config::new);
        spec = specPair.getRight();
        config = specPair.getLeft();
    }

    public final ModConfigSpec.ConfigValue<? extends String> forwardingSecret;

    Config(ModConfigSpec.Builder builder) {
        builder.comment("Modern Forwarding Settings").push("modernForwarding");

        forwardingSecret = builder.define("forwardingSecret", "");

        builder.pop();
    }

    public static void setupForwarding() {
        String forwardingSecret = Config.config.forwardingSecret.get();
        if (!(forwardingSecret.isBlank() || forwardingSecret.isEmpty())) {
            PCF.modernForwarding = new ModernForwarding(forwardingSecret);
        }
    }
}
