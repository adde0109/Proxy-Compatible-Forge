package org.adde0109.pcf.common.reflection;

import dev.neuralnexus.taterapi.MinecraftVersion;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

public class StateUtil {
    private static final VarHandle cachedStateField;
    private static final Enum<?>[] cachedStateEnumConstants;

    static {
        MinecraftVersion mcv = MinecraftVersion.get();
        String slplClassName = "net.minecraft.server.network.ServerLoginPacketListenerImpl";
        String stateFieldName = "state";
        if (mcv.isInRange(MinecraftVersion.V1_14_4, MinecraftVersion.V1_16_5)) {
            stateFieldName = "field_147328_g";
            slplClassName = "net.minecraft.network.login.ServerLoginNetHandler";
        } else if (mcv.isInRange(MinecraftVersion.V1_17, MinecraftVersion.V1_20_4)) {
            stateFieldName = "f_10019_";
        }
        String slplStateClassName = slplClassName + "$State";

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            // net.minecraft.server.network.ServerLoginPacketListenerImpl
            Class<?> slplClass = Class.forName(slplClassName);
            // private ServerLoginPacketListenerImpl#state
            Field stateField = slplClass.getDeclaredField(stateFieldName);
            stateField.setAccessible(true);
            MethodHandles.Lookup privLookup = MethodHandles.privateLookupIn(slplClass, lookup);
            cachedStateField = privLookup.unreflectVarHandle(stateField);
            // ServerLoginPacketListenerImpl$State
            Class<?> cachedStateClass = Class.forName(slplStateClassName);
            cachedStateEnumConstants = (Enum<?>[]) cachedStateClass.getEnumConstants();
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getState(Object slpl) {
        return cachedStateField.get(slpl);
    }

    public static void setState(Object slpl, int ord) {
        Object state = cachedStateEnumConstants[ord];
        cachedStateField.set(slpl, state);
    }

    public static boolean stateEquals(Object slpl, int ord) {
        Object e = getState(slpl);
        return ((Enum<?>) e).ordinal() == ord;
    }
}
