package org.adde0109.pcf;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ArgumentTypesEntryUtil {
    private static Method cachedATGet;
    private static Field cachedATEntrySerializer;
    private static Field cachedATEntryName;

    private static void cacheReflection() {
        try {
            // net.minecraft.commands.synchronization.ArgumentTypes (net.minecraft.src.C_4657_)
            Class<?> ATClass = Class.forName("net.minecraft.commands.synchronization.ArgumentTypes");
            // static ArgumentTypes$Entry<?> ArgumentTypes.get(ArgumentType<?>) (C_4657_#m_121616_)
            cachedATGet = ATClass.getDeclaredMethod("m_121616_", ArgumentType.class);
            cachedATGet.setAccessible(true);
            // ArgumentTypes$Entry (C_4657_$C_4659_)
            Class<?> ATEntryClass = Class.forName("net.minecraft.commands.synchronization.ArgumentTypes$Entry");
            // ArgumentSerializer<T> ArgumentTypes$Entry#serializer (C_4657_$C_4659_#f_121619_)
            cachedATEntrySerializer = ATEntryClass.getDeclaredField("f_121619_");
            // ResourceLocation ArgumentTypes$Entry#name (C_4657_$C_4659_#f_121620_)
            cachedATEntryName = ATEntryClass.getDeclaredField("f_121620_");
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends ArgumentType<?>> Object getEntry(T argumentType) {
        if (cachedATGet == null) {
            cacheReflection();
        }
        try {
            return cachedATGet.invoke(null, argumentType);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends ArgumentType<?>> ArgumentSerializer<T> getSerializer(Object entry) {
        if (cachedATEntrySerializer == null) {
            cacheReflection();
        }
        try {
            return (ArgumentSerializer<T>) cachedATEntrySerializer.get(entry);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResourceLocation getName(Object entry) {
        if (cachedATEntryName == null) {
            cacheReflection();
        }
        try {
            return (ResourceLocation) cachedATEntryName.get(entry);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
