package org.adde0109.pcf.v16_5.forge;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;
import dev.neuralnexus.taterapi.network.NetworkAdapters;

import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.FMLNetworkConstants;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.crossstitch.CrossStitch;
import org.adde0109.pcf.mixin.v16_5.forge.crossstitch.ArgumentTypesAccessor;
import org.adde0109.pcf.v16_5.forge.forwarding.network.CCustomQueryPacketAdapter;
import org.adde0109.pcf.v16_5.forge.forwarding.network.SCustomQueryAnswerPacketAdapter;
import org.apache.commons.lang3.tuple.Pair;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V14))
public final class Initializer implements PCFInitializer {
    public Initializer() {
        NetworkAdapters.register(
                CCustomQueryPacketAdapter.INSTANCE, SCustomQueryAnswerPacketAdapter.INSTANCE);

        CrossStitch.GET_ARGUMENT_TYPE_ENTRY =
                (argumentType) -> ArgumentTypesAccessor.pcf$get((ArgumentType<?>) argumentType);
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
