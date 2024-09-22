package org.adde0109.pcf;

import java.lang.reflect.Field;

public class StateUtil {
    private static Field cachedStateField;
    private static Enum<?>[] cachedStateEnumConstants;

    private static void cacheField() {
        try {
            // net.minecraft.server.network.ServerLoginPacketListenerImpl (net.minecraft.src.C_37_)
            Class<?> cachedSLPLClass = Class.forName("net.minecraft.server.network.ServerLoginPacketListenerImpl");
            // private ServerLoginPacketListenerImpl#state (net.minecraft.src.C_37_#f_10019_)
            cachedStateField = cachedSLPLClass.getDeclaredField("f_10019_");
            cachedStateField.setAccessible(true);
            // ServerLoginPacketListenerImpl$State (net/minecraft/src/C_37_$C_38_) (enum)
            Class<?> cachedStateClass = Class.forName("net.minecraft.server.network.ServerLoginPacketListenerImpl$State");
            cachedStateEnumConstants = (Enum<?>[]) cachedStateClass.getEnumConstants();
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getState(Object slpl) {
        if (cachedStateField == null) {
            cacheField();
        }
        try {
            return cachedStateField.get(slpl);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setState(Object slpl, int ord) {
        Object state = cachedStateEnumConstants[ord];
        if (cachedStateField == null) {
            cacheField();
        }
        try {
            cachedStateField.set(slpl, state);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean stateEquals(Object slpl, int ord) {
        Object e = getState(slpl);
        return ((Enum<?>) e).ordinal() == ord;
    }
}
