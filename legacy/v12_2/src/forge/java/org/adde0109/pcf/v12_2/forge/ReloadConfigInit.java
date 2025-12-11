package org.adde0109.pcf.v12_2.forge;

import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;

@AConstraint(
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V12, max = MinecraftVersion.V12_2))
@EventBusSubscriber
public final class ReloadConfigInit implements PCFInitializer {
    @Override
    public void onInit() {
        MinecraftForge.EVENT_BUS.register(this);
        ConfigManager.load(PCF.MOD_ID, Type.INSTANCE);
    }

    @SubscribeEvent
    public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(PCF.MOD_ID)) {
            ModConfig.reload();
        }
    }
}
