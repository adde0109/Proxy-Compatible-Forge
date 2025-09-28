package org.adde0109.pcf.v1_20_2.neoforge;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.Platforms;

import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkAddon;

import org.adde0109.pcf.PCF;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;

public final class Compatibility {
    public static final boolean isNeoForge1_20_2;
    private static final boolean shouldApplyFFAPIFix;

    static {
        MetaAPI api = MetaAPI.instance();
        isNeoForge1_20_2 =
                api.isPlatformPresent(Platforms.NEOFORGE)
                        && api.version().is(MinecraftVersions.V20_2);
        shouldApplyFFAPIFix =
                api.isPlatformPresent(Platforms.NEOFORGE)
                        && api.version().isAtLeast(MinecraftVersions.V21_1)
                        && api.isModLoaded(Platforms.NEOFORGE, "fabric_networking_api_v1");
    }

    /**
     * Remove PCF's query id from FFAPI's channels map to prevent infinite login screen
     *
     * @param serverLoginPacketListener an instance of ServerLoginPacketListenerImpl
     */
    @SuppressWarnings({"UnstableApiUsage", "unchecked"})
    public static void applyFFAPIFix(Object serverLoginPacketListener) {
        if (!shouldApplyFFAPIFix) {
            return;
        }
        // spotless:off
        try {
            ServerLoginNetworkAddon addon = (ServerLoginNetworkAddon)
                    ((NetworkHandlerExtensions) serverLoginPacketListener).getAddon();
            MethodHandles.Lookup slnaLookup =
                    MethodHandles.privateLookupIn(ServerLoginNetworkAddon.class, MethodHandles.publicLookup());
            MethodHandle channelsMH =
                    slnaLookup.findGetter(ServerLoginNetworkAddon.class, "channels", Map.class);
            Map<Integer, ?> channels = (Map<Integer, ?>) channelsMH.invokeExact(addon);

            channels.remove(PCF.QUERY_ID);
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
            PCF.logger.warn("Lookup Exception applying FFAPI fix", e);
        } catch (Throwable e) {
            PCF.logger.warn("Exception applying FFAPI fix", e);
        }
        // spotless:on
    }
}
