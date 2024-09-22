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
            // net.minecraft.commands.synchronization.ArgumentTypes (net.minecraft.command.arguments.ArgumentTypes)
            Class<?> ATClass = Class.forName("net.minecraft.command.arguments.ArgumentTypes");
            // static ArgumentTypes$Entry<?> ArgumentTypes.get(ArgumentType<?>) (ArgumentTypes#func_201040_a)
            cachedATGet = ATClass.getDeclaredMethod("func_201040_a", ArgumentType.class);
            cachedATGet.setAccessible(true);
            // ArgumentTypes$Entry
            Class<?> ATEntryClass = Class.forName("net.minecraft.command.arguments.ArgumentTypes$Entry");
            // ArgumentSerializer<T> ArgumentTypes$Entry#serializer (ArgumentTypes$Entry#field_197480_b)
            cachedATEntrySerializer = ATEntryClass.getDeclaredField("field_197480_b");
            // ResourceLocation ArgumentTypes$Entry#name (C_4657_$C_4659_#field_197481_c)
            cachedATEntryName = ATEntryClass.getDeclaredField("field_197481_c");
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
