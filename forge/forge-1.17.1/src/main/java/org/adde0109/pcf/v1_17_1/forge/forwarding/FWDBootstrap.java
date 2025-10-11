package org.adde0109.pcf.v1_17_1.forge.forwarding;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public final class FWDBootstrap {
    private FWDBootstrap() {}

    public static Function<String, ResourceLocation> RESOURCE_LOCATION;
    public static Function<String, Component> COMPONENT;

    private static final String PLAYER_INFO_CHANNEL_ID = "velocity:player_info";
    public static ResourceLocation PLAYER_INFO_CHANNEL;

    private static final String DIRECT_CONN_ERR_MSG = "Direct connections to this server are not permitted!";
    public static Component DIRECT_CONN_ERR;

    public static void init() {
        PLAYER_INFO_CHANNEL = RESOURCE_LOCATION.apply(PLAYER_INFO_CHANNEL_ID);
        DIRECT_CONN_ERR = COMPONENT.apply(DIRECT_CONN_ERR_MSG);
    }
}
