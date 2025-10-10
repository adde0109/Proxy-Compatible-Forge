package org.adde0109.pcf.v1_14_4.forge.crossstitch;

import net.minecraftforge.server.command.EnumArgument;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
public final class DummyEnumArgumentSupplier implements Supplier<EnumArgument> {
    @SuppressWarnings("rawtypes")
    private static final Constructor<EnumArgument> enumArgumentConstructor;

    private static final Field enumClassField;

    static {
        try {
            enumArgumentConstructor = EnumArgument.class.getDeclaredConstructor(Class.class);
            enumArgumentConstructor.setAccessible(true);

            enumClassField = EnumArgument.class.getDeclaredField("enumClass");
            enumClassField.setAccessible(true);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<Enum<?>> getEnumClass(EnumArgument<?> argument) {
        try {
            return (Class<Enum<?>>) enumClassField.get(argument);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private EnumArgument<?> createEnumArgument(Class<?> enumClass) {
        try {
            return enumArgumentConstructor.newInstance(enumClass);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EnumArgument get() {
        return createEnumArgument(Enum.class);
    }
}
