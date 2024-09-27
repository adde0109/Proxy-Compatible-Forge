package org.adde0109.pcf.v1_20_2.neoforge;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

public class StateUtil {
    private static VarHandle cachedStateField;
    private static Enum<?>[] cachedStateEnumConstants;

    private static void cacheField() {
        try {
            // private ServerLoginPacketListenerImpl#state
            Field stateField = ServerLoginPacketListenerImpl.class.getDeclaredField("state");
            stateField.setAccessible(true);
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(ServerLoginPacketListenerImpl.class, MethodHandles.lookup());
            cachedStateField = lookup.unreflectVarHandle(stateField);
            // ServerLoginPacketListenerImpl$State
            Class<?> cachedStateClass = Class.forName("net.minecraft.server.network.ServerLoginPacketListenerImpl$State");
            cachedStateEnumConstants = (Enum<?>[]) cachedStateClass.getEnumConstants();
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getState(Object slpl) {
        if (cachedStateField == null) {
            cacheField();
        }
        return cachedStateField.get(slpl);
    }

    public static void setState(Object slpl, int ord) {
        Object state = cachedStateEnumConstants[ord];
        if (cachedStateField == null) {
            cacheField();
        }
        cachedStateField.set(slpl, state);
    }

    public static boolean stateEquals(Object slpl, int ord) {
        Object e = getState(slpl);
        return ((Enum<?>) e).ordinal() == ord;
    }
}
