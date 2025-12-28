package org.adde0109.pcf.v16_5.forge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.FMLNetworkConstants;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v14_4.forge.Config;
import org.adde0109.pcf.v14_4.forge.forwarding.network.CCustomQueryPacketAdapter;
import org.adde0109.pcf.v14_4.forge.forwarding.network.SCustomQueryAnswerPacketAdapter;
import org.apache.commons.lang3.tuple.Pair;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V14, max = MinecraftVersion.V16_5))
public final class Initializer implements PCFInitializer {
    public Initializer() {
        PCF.instance()
                .adapters()
                .register(
                        CCustomQueryPacketAdapter.INSTANCE,
                        SCustomQueryAnswerPacketAdapter.INSTANCE);
    }

    @Override
    public void onInit() {
        this.displayTest();
        this.registerConfig();
    }

    private void displayTest() {
        ModLoadingContext.get()
                .registerExtensionPoint(
                        ExtensionPoint.DISPLAYTEST,
                        () ->
                                Pair.of(
                                        () -> FMLNetworkConstants.IGNORESERVERONLY,
                                        (remoteVersion, isFromServer) -> true));
    }

    private void registerConfig() {
        ModLoadingContext.get()
                .registerConfig(ModConfig.Type.COMMON, Config.spec, PCF.CONFIG_FILE_NAME);
    }
}
