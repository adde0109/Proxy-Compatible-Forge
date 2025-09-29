package org.adde0109.pcf.v1_14_4.forge.reflection;

import com.mojang.brigadier.arguments.ArgumentType;

import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCF;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class ArgumentTypesUtil {
    private static final Field byClassField;
    private static final Field byNameField;
    private static final Method getAtMethod;
    private static final Constructor<?> entryConstructor;
    private static final Field entrySerializerField;
    private static final Field entryNameField;

    static {
        try {
            // private Map<Class<?>, ArgumentTypes$Entry<?>> ArgumentTypes.BY_CLASS
            // (ArgumentTypes#field_197489_b)
            byClassField = ArgumentTypes.class.getDeclaredField("field_197489_b");
            byClassField.setAccessible(true);

            // private Map<ResourceLocation, ArgumentTypes$Entry<?>> ArgumentTypes.BY_NAME
            // (ArgumentTypes#field_197490_c)
            byNameField = ArgumentTypes.class.getDeclaredField("field_197490_c");
            byNameField.setAccessible(true);

            // private static ArgumentTypes$Entry<?> ArgumentTypes.get(ArgumentType<?>)
            // (ArgumentTypes#func_201040_a)
            getAtMethod =
                    ArgumentTypes.class.getDeclaredMethod("func_201040_a", ArgumentType.class);
            getAtMethod.setAccessible(true);

            // ArgumentTypes$Entry
            Class<?> ATEntryClass =
                    Class.forName("net.minecraft.command.arguments.ArgumentTypes$Entry");

            // ArgumentTypes$Entry constructor
            entryConstructor =
                    ATEntryClass.getDeclaredConstructor(
                            Class.class, ArgumentSerializer.class, ResourceLocation.class);
            entryConstructor.setAccessible(true);

            // ArgumentSerializer<T> ArgumentTypes$Entry#serializer
            // (ArgumentTypes$Entry#field_197480_b)
            entrySerializerField = ATEntryClass.getDeclaredField("field_197480_b");
            entrySerializerField.setAccessible(true);

            // ResourceLocation ArgumentTypes$Entry#name
            // (ArgumentTypes$Entry#field_197481_c)
            entryNameField = ATEntryClass.getDeclaredField("field_197481_c");
            entryNameField.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
            PCF.logger.error("Error setting up ArgumentTypesEntryUtil", e);
            throw new RuntimeException(e);
        }
    }

    public static <T extends ArgumentType<?>> Object getEntry(T argumentType) {
        try {
            return getAtMethod.invoke(null, argumentType);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<Class<?>, Object> getByClassMap() {
        try {
            return (Map<Class<?>, Object>) byClassField.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<ResourceLocation, Object> getByNameMap() {
        try {
            return (Map<ResourceLocation, Object>) byNameField.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends ArgumentType<?>> Object createEntry(
            Class<T> argumentClass, ArgumentSerializer<T> serializer, ResourceLocation name) {
        try {
            return entryConstructor.newInstance(argumentClass, serializer, name);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static ArgumentSerializer<ArgumentType<?>> getSerializer(Object entry) {
        try {
            return (ArgumentSerializer<ArgumentType<?>>) entrySerializerField.get(entry);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResourceLocation getName(Object entry) {
        try {
            return (ResourceLocation) entryNameField.get(entry);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
