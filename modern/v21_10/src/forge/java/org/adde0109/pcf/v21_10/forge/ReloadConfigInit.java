package org.adde0109.pcf.v21_10.forge;

import dev.neuralnexus.taterapi.Wrapped;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v14_4.forge.Config;

@AConstraint(platform = Platform.FORGE, version = @Versions(min = MinecraftVersion.V21_6))
public final class ReloadConfigInit implements PCFInitializer {
    @Override
    public void onInit() {
        BusGroup modBusGroup =
                MetaAPI.instance()
                        .meta()
                        .<FMLModContainer>mod(PCF.MOD_ID)
                        .map(Wrapped::unwrap)
                        .map(FMLModContainer::getModBusGroup)
                        .orElseThrow();
        ModConfigEvent.Reloading.getBus(modBusGroup)
                .addListener((ModConfigEvent.Reloading event) -> Config.reload());
    }
}
