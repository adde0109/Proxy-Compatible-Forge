package org.adde0109.pcf.v1_14_4.forge.reflection;

import com.mojang.brigadier.arguments.ArgumentType;

import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCF;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ArgumentTypesEntryUtil {
    private static final Method cachedATGet;
    private static final Field cachedATEntrySerializer;
    private static final Field cachedATEntryName;

    static {
        try {
            // private static ArgumentTypes$Entry<?> ArgumentTypes.get(ArgumentType<?>)
            // (ArgumentTypes#func_201040_a)
            cachedATGet =
                    ArgumentTypes.class.getDeclaredMethod("func_201040_a", ArgumentType.class);
            cachedATGet.setAccessible(true);
            // ArgumentTypes$Entry
            Class<?> ATEntryClass =
                    Class.forName("net.minecraft.command.arguments.ArgumentTypes$Entry");

            // ArgumentSerializer<T> ArgumentTypes$Entry#serializer
            // (ArgumentTypes$Entry#field_197480_b)
            cachedATEntrySerializer = ATEntryClass.getDeclaredField("field_197480_b");
            cachedATEntrySerializer.setAccessible(true);

            // ResourceLocation ArgumentTypes$Entry#name
            // (ArgumentTypes$Entry#field_197481_c)
            cachedATEntryName = ATEntryClass.getDeclaredField("field_197481_c");
            cachedATEntryName.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
            PCF.logger.error("Error setting up ArgumentTypesEntryUtil", e);
            throw new RuntimeException(e);
        }
    }

    public static <T extends ArgumentType<?>> Object getEntry(T argumentType) {
        try {
            return cachedATGet.invoke(null, argumentType);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static ArgumentSerializer<ArgumentType<?>> getSerializer(Object entry) {
        try {
            return (ArgumentSerializer<ArgumentType<?>>) cachedATEntrySerializer.get(entry);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResourceLocation getName(Object entry) {
        try {
            return (ResourceLocation) cachedATEntryName.get(entry);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
