package org.adde0109.pcf;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import net.minecraftforge.registries.ForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

import static net.minecraftforge.registries.ForgeRegistry.REGISTRIES;

public class HandshakeHandler {

    static final Marker FMLHSMARKER = MarkerManager.getMarker("PCFHANDSHAKE");
    private static final Logger LOGGER = LogManager.getLogger();

    private Set<ResourceLocation> registriesToReceive;
    private Map<ResourceLocation, ForgeRegistry.Snapshot> registrySnapshots;

    private static final Field registrySnapshotsField;

    private static final SimpleChannel handshakeChannel;

    private static final Method validateClientChannelsMethod;
    private static final Method handleRegistryLoadingMethod;

    static {
        try {
            Field field = NetworkConstants.class.getDeclaredField("handshakeChannel");
            field.setAccessible(true);
            handshakeChannel = (SimpleChannel) field.get(null);

            validateClientChannelsMethod = NetworkRegistry.class
                    .getDeclaredMethod("validateClientChannels", Map.class);
            validateClientChannelsMethod.setAccessible(true);

            handleRegistryLoadingMethod = net.minecraftforge.network.HandshakeHandler.class
                    .getDeclaredMethod("handleRegistryLoading", Supplier.class);
            handleRegistryLoadingMethod.setAccessible(true);

            registrySnapshotsField = net.minecraftforge.network.HandshakeHandler.class
                    .getDeclaredField("registrySnapshots");
            registrySnapshotsField.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<ResourceLocation, String> validateClientChannels(Map<ResourceLocation, String> channels) {
        try {
            return (Map<ResourceLocation, String>) validateClientChannelsMethod.invoke(null, channels);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean handleRegistryLoading(net.minecraftforge.network.HandshakeHandler handler,
                                                 Supplier<NetworkEvent.Context> contextSupplier) {
        try {
            return (boolean) handleRegistryLoadingMethod.invoke(handler, contextSupplier);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    void handleModListProposalOnClient(net.minecraftforge.network.HandshakeHandler handler,
                                       HandshakeMessages.S2CModList serverModList,
                                       Supplier<NetworkEvent.Context> c) {
        LOGGER.debug(FMLHSMARKER, "Validating mod list [{}]", String.join(", ", serverModList.getModList()));
        Map<ResourceLocation, String> mismatchedChannels = validateClientChannels(serverModList.getChannels());
        c.get().setPacketHandled(true);
        if (!mismatchedChannels.isEmpty()) {
            handshakeChannel.reply(new HandshakeMessages.C2SAcknowledge(), c.get());
            return;
        }
        // Validate synced custom datapack registries, client cannot be missing any present on the server.
        List<String> missingDataPackRegistries = new ArrayList<>();
        Set<ResourceKey<? extends Registry<?>>> clientDataPackRegistries = DataPackRegistriesHooks.getSyncedCustomRegistries();
        for (ResourceKey<? extends Registry<?>> key : serverModList.getCustomDataPackRegistries()) {
            if (!clientDataPackRegistries.contains(key)) {
                ResourceLocation location = key.location();
                LOGGER.error(FMLHSMARKER, "Missing required datapack registry: {}", location);
                missingDataPackRegistries.add(key.location().toString());
            }
        }
        if (!missingDataPackRegistries.isEmpty()) {
            handshakeChannel.reply(new HandshakeMessages.C2SAcknowledge(), c.get());
            return;
        }
        LOGGER.debug(FMLHSMARKER, "Mod list successfully validated, sending client modlist");
        handshakeChannel.reply(new HandshakeMessages.C2SModListReply(), c.get());

        if (!serverModList.getRegistries().isEmpty()) {
            LOGGER.debug(FMLHSMARKER, "Preparing for reset");
            this.registriesToReceive = new HashSet<>(serverModList.getRegistries());
            this.registrySnapshots = Maps.newHashMap();
            try {
                registrySnapshotsField.set(handler ,this.registrySnapshots);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            LOGGER.debug(REGISTRIES, "Expecting {} registries: {}", () -> this.registriesToReceive.size(), () -> this.registriesToReceive);
        }
    }

    private void reset() {
        Minecraft instance = Minecraft.getInstance();
        if (instance.level == null) {
            return;
        }
        LOGGER.debug(FMLHSMARKER, "Starting reset.");
        net.minecraftforge.client.ForgeHooksClient.firePlayerLogout(instance.gameMode, instance.player);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.level.LevelEvent.Unload(instance.level));
        net.minecraftforge.client.ForgeHooksClient.handleClientLevelClosing(instance.level);
    }

    void handleRegistryMessage(net.minecraftforge.network.HandshakeHandler handler,
                               final HandshakeMessages.S2CRegistry registryPacket,
                               final Supplier<NetworkEvent.Context> contextSupplier) {
        LOGGER.debug(FMLHSMARKER, "Received registry packet for {}", registryPacket.getRegistryName());
        this.registriesToReceive.remove(registryPacket.getRegistryName());
        this.registrySnapshots.put(registryPacket.getRegistryName(), registryPacket.getSnapshot());

        boolean continueHandshake = true;
        if (this.registriesToReceive.isEmpty()) {
            contextSupplier.get().enqueueWork(this::reset);
            continueHandshake = handleRegistryLoading(handler, contextSupplier);
        }
        // The handshake reply isn't sent until we have processed the message
        contextSupplier.get().setPacketHandled(true);
        if (!continueHandshake) {
            LOGGER.error(FMLHSMARKER, "Connection closed, not continuing handshake");
        } else {
            handshakeChannel.reply(new HandshakeMessages.C2SAcknowledge(), contextSupplier.get());
        }

    }

    void handleConfigSync(net.minecraftforge.network.HandshakeHandler handler,
                          final HandshakeMessages.S2CConfigData msg,
                          final Supplier<NetworkEvent.Context> contextSupplier) {
        LOGGER.debug(FMLHSMARKER, "Received config sync from server");
        ConfigSync.INSTANCE.receiveSyncedConfig(msg, contextSupplier);
        contextSupplier.get().setPacketHandled(true);
        handshakeChannel.reply(new HandshakeMessages.C2SAcknowledge(), contextSupplier.get());
    }
}
