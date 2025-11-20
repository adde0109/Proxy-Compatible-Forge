package org.adde0109.pcf.v1_14_4.forge;

import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;

import org.adde0109.pcf.PCFInitializer;
import org.apache.commons.lang3.tuple.Pair;

@AConstraint(
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V14, max = MinecraftVersion.V16_5))
public final class DisplayTest implements PCFInitializer {
    @Override
    public void onInit() {
        ModLoadingContext.get()
                .registerExtensionPoint(
                        ExtensionPoint.DISPLAYTEST,
                        () ->
                                Pair.of(
                                        () -> FMLNetworkConstants.IGNORESERVERONLY,
                                        (remoteVersion, isFromServer) -> true));
    }
}
