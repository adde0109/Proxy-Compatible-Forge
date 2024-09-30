package org.adde0109.pcf.v1_14_4.forge;

import net.minecraftforge.common.ForgeConfigSpec;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.ModernForwarding;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    public static final Config config;
    public static final ForgeConfigSpec spec;

    static {
        final Pair<Config, ForgeConfigSpec> specPair =
                new ForgeConfigSpec.Builder().configure(Config::new);
        spec = specPair.getRight();
        config = specPair.getLeft();
    }

    public final ForgeConfigSpec.ConfigValue<? extends String> forwardingSecret;

    Config(ForgeConfigSpec.Builder builder) {
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
