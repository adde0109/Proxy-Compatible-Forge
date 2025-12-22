package org.adde0109.pcf.common;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import org.adde0109.pcf.PCF;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Function;

@SuppressWarnings("JavaLangInvokeHandleSignature")
public final class Identifier {
    private static MethodHandle newIdentifier;

    public static final Function<String, ?> IDENTIFIER =
            id -> {
                try {
                    return newIdentifier.invoke(id);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };

    // spotless:off
    static {
        try {
            if (Constraint.builder().max(MinecraftVersions.V16_5).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.util.ResourceLocation");
                newIdentifier = MethodHandles.lookup().findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V17)
                    .max(MinecraftVersions.V20_4).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.resources.ResourceLocation");
                newIdentifier = MethodHandles.lookup().findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V20_5)
                    .max(MinecraftVersions.V21_10).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.resources.ResourceLocation");
                newIdentifier = MethodHandles.lookup().findStatic(clazz, "parse", MethodType.methodType(clazz, String.class));
            } else if (Constraint.builder().min(MinecraftVersions.V21_11).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.resources.Identifier");
                newIdentifier = MethodHandles.lookup().findStatic(clazz, "parse", MethodType.methodType(clazz, String.class));
            }
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            PCF.logger.error("Failed to initialize Identifier function", e);
        }
    }
    // spotless:on

    @SuppressWarnings("unchecked")
    public static <T> T identifier(String id) {
        return (T) IDENTIFIER.apply(id);
    }
}
