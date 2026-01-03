package org.adde0109.pcf.common.reflection;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ArgumentTypesEntryUtil {
    private static final MethodHandle cachedATGet;
    private static final VarHandle cachedATEntrySerializer;
    private static final VarHandle cachedATEntryName;

    static {
        String atClassName = "net.minecraft.commands.synchronization.ArgumentTypes";
        String atEntryClassName = "net.minecraft.commands.synchronization.ArgumentTypes$Entry";
        String atGetMethodName = "m_121616_";
        String atEntrySerializerFieldName = "f_121619_";
        String atEntryNameFieldName = "f_121620_";
        if (Constraint.range(MinecraftVersions.V14, MinecraftVersions.V16_5).result()) {
            atClassName = "net.minecraft.command.arguments.ArgumentTypes";
            atEntryClassName = "net.minecraft.command.arguments.ArgumentTypes$Entry";
            atGetMethodName = "func_201040_a";
            atEntrySerializerFieldName = "field_197480_b";
            atEntryNameFieldName = "field_197481_c";
        }

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            // net.minecraft.commands.synchronization.ArgumentTypes
            Class<?> atClass = Class.forName(atClassName);
            // static ArgumentTypes$Entry<?> ArgumentTypes.get(ArgumentType<?>)
            Method atGetMethod =
                    atClass.getDeclaredMethod(
                            atGetMethodName,
                            Class.forName("com.mojang.brigadier.arguments.ArgumentType"));
            atGetMethod.setAccessible(true);
            MethodHandles.Lookup privLookup = MethodHandles.privateLookupIn(atClass, lookup);
            cachedATGet = privLookup.unreflect(atGetMethod);
            // ArgumentTypes$Entry
            Class<?> atEntryClass = Class.forName(atEntryClassName);
            privLookup = MethodHandles.privateLookupIn(atEntryClass, lookup);
            // ArgumentSerializer<T> ArgumentTypes$Entry#serializer
            Field atEntrySerializerField =
                    atEntryClass.getDeclaredField(atEntrySerializerFieldName);
            cachedATEntrySerializer = privLookup.unreflectVarHandle(atEntrySerializerField);
            // ResourceLocation ArgumentTypes$Entry#name
            Field atEntryNameField = atEntryClass.getDeclaredField(atEntryNameFieldName);
            cachedATEntryName = privLookup.unreflectVarHandle(atEntryNameField);
        } catch (ClassNotFoundException
                | IllegalAccessException
                | NoSuchFieldException
                | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Object getEntry(T argumentType) {
        try {
            return cachedATGet.invoke(argumentType);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getSerializer(Object entry) {
        return cachedATEntrySerializer.get(entry);
    }

    public static Object getName(Object entry) {
        return cachedATEntryName.get(entry);
    }
}
