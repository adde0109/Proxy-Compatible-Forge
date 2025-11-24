package org.adde0109.pcf.v1_20_2.neoforge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v1_20_2.neoforge.crossstitch.CSBootstrap;

@AConstraint(
        mappings = Mappings.MOJANG,
        platform = Platform.NEOFORGE,
        version = @Versions(min = MinecraftVersion.V20_2))
public final class CmdArgTypeKeyInit implements PCFInitializer {
    public CmdArgTypeKeyInit() {
        CSBootstrap.COMMAND_ARGUMENT_TYPE_KEY =
                (type) ->
                        CSBootstrap.ARGUMENT_TYPES_REGISTRY
                                .get()
                                .flatMap(reg -> reg.getResourceKey(type));
    }

    @Override
    public void onInit() {}
}
