package org.adde0109.pcf.v12_2.forge.reflection;

import java.lang.reflect.Field;

public class StateUtil {
    private static Field cachedStateField;
    private static Enum<?>[] cachedStateEnumConstants;

    private static void cacheField() {
        try {
            // net.minecraft.server.network.NetHandlerLoginServer
            Class<?> cachedSLPLClass =
                    Class.forName("net.minecraft.server.network.NetHandlerLoginServer");
            // private NetHandlerLoginServer#currentLoginState
            // (NetHandlerLoginServer#field_147328_g)
            cachedStateField = cachedSLPLClass.getDeclaredField("field_147328_g");
            cachedStateField.setAccessible(true);
            // NetHandlerLoginServer$LoginState
            Class<?> cachedStateClass =
                    Class.forName("net.minecraft.server.network.NetHandlerLoginServer$LoginState");
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
