package org.adde0109.pcf.v14_4.forge.reflection;

import java.lang.reflect.Field;

public class StateUtil {
    private static Field cachedStateField;
    private static Enum<?>[] cachedStateEnumConstants;

    private static void cacheField() {
        try {
            // net.minecraft.server.network.ServerLoginPacketListenerImpl
            // (net.minecraft.network.login.ServerLoginNetHandler)
            Class<?> cachedSLPLClass =
                    Class.forName("net.minecraft.network.login.ServerLoginNetHandler");
            // private ServerLoginPacketListenerImpl#state (ServerLoginNetHandler#field_147328_g)
            cachedStateField = cachedSLPLClass.getDeclaredField("field_147328_g");
            cachedStateField.setAccessible(true);
            // ServerLoginPacketListenerImpl$State (ServerLoginNetHandler$State)
            Class<?> cachedStateClass =
                    Class.forName("net.minecraft.network.login.ServerLoginNetHandler$State");
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
