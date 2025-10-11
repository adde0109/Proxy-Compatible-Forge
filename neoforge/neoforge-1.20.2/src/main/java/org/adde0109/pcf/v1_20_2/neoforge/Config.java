package org.adde0109.pcf.v1_20_2.neoforge;

import net.neoforged.neoforge.common.ModConfigSpec;

import org.adde0109.pcf.PCF;
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

    public final ModConfigSpec.ConfigValue<? extends String> forwardingSecret;
    public final ModConfigSpec.ConfigValue<List<? extends String>> moddedArgumentTypes;

    Config(ModConfigSpec.Builder builder) {
        builder.comment("Modern Forwarding Settings").push("modernForwarding");
        forwardingSecret = builder.define("forwardingSecret", "");
        builder.pop();

        builder.push("commandWrapping");
        moddedArgumentTypes =
                builder.comment(
                                "List of argument types that are not vanilla but are integrated into the server (found in the Vanilla registry)")
                        .defineList(
                                "moddedArgumentTypes",
                                List.of("livingthings:sampler_types"),
                                (obj) -> true);
        builder.pop();
    }

    public static void setupForwarding() {
        String forwardingSecret = Config.config.forwardingSecret.get();
        if (!forwardingSecret.isBlank()) {
            PCF.instance().setForwardingSecret(forwardingSecret);
        }
    }

    @SuppressWarnings("unchecked")
    public static void setupModdedArgumentTypes() {
        List<String> moddedArgumentTypes = (List<String>) Config.config.moddedArgumentTypes.get();
        PCF.instance().addModdedArgumentTypes(moddedArgumentTypes);
    }
}
