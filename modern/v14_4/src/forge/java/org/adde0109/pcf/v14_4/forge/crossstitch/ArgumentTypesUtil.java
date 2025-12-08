package org.adde0109.pcf.v14_4.forge.crossstitch;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.meta.MetaAPI;

import org.adde0109.pcf.PCF;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public final class ArgumentTypesUtil {
    private static final Field byClassField;
    private static final Field byNameField;
    private static final Method getAtMethod;
    private static final Field entrySerializerField;
    private static final Field entryNameField;

    static {
        try {
            String argTypesClassName;
            String byClassFieldName;
            String byNameFieldName;
            String getAtMethodName;

            String entrySerializerFieldName;
            String entryNameFieldName;

            switch (MetaAPI.instance().mappings()) {
                case LEGACY_SEARGE -> {
                    argTypesClassName = "net.minecraft.command.arguments.ArgumentTypes";
                    byClassFieldName = "field_197489_b";
                    byNameFieldName = "field_197490_c";
                    getAtMethodName = "func_201040_a";
                    entrySerializerFieldName = "field_197480_b";
                    entryNameFieldName = "field_197481_c";
                }
                case SEARGE -> {
                    argTypesClassName = "net.minecraft.commands.synchronization.ArgumentTypes";
                    byClassFieldName = "f_121583_";
                    byNameFieldName = "f_121584_";
                    getAtMethodName = "m_121616_";
                    entrySerializerFieldName = "f_121619_";
                    entryNameFieldName = "f_121620_";
                }
                default -> {
                    argTypesClassName = "net.minecraft.commands.synchronization.ArgumentTypes";
                    byClassFieldName = "BY_CLASS";
                    byNameFieldName = "BY_NAME";
                    getAtMethodName = "get";
                    entrySerializerFieldName = "serializer";
                    entryNameFieldName = "name";
                }
            }
            // ArgumentTypes class
            Class<?> ArgumentTypes = Class.forName(argTypesClassName);

            // private Map<Class<?>, ArgumentTypes$Entry<?>> ArgumentTypes.BY_CLASS
            byClassField = ArgumentTypes.getDeclaredField(byClassFieldName);
            byClassField.setAccessible(true);

            // private Map<ResourceLocation, ArgumentTypes$Entry<?>> ArgumentTypes.BY_NAME
            byNameField = ArgumentTypes.getDeclaredField(byNameFieldName);
            byNameField.setAccessible(true);

            // private static ArgumentTypes$Entry<?> ArgumentTypes.get(ArgumentType<?>)
            getAtMethod = ArgumentTypes.getDeclaredMethod(getAtMethodName, ArgumentType.class);
            getAtMethod.setAccessible(true);

            // ArgumentTypes$Entry
            Class<?> ATEntryClass = Class.forName(argTypesClassName + "$Entry");

            // ArgumentSerializer<T> ArgumentTypes$Entry#serializer
            entrySerializerField = ATEntryClass.getDeclaredField(entrySerializerFieldName);
            entrySerializerField.setAccessible(true);

            // ResourceLocation ArgumentTypes$Entry#name
            entryNameField = ATEntryClass.getDeclaredField(entryNameFieldName);
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
    public static Map<?, Object> getByNameMap() {
        try {
            return (Map<?, Object>) byNameField.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getSerializer(Object entry) {
        try {
            return entrySerializerField.get(entry);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getName(Object entry) {
        try {
            return entryNameField.get(entry);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
