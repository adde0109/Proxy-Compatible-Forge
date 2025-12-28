package org.adde0109.pcf.common;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import org.adde0109.pcf.PCF;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Function;

@SuppressWarnings("JavaLangInvokeHandleSignature")
public final class Component {
    private static MethodHandle newLiteral;
    private static MethodHandle newTranslatable;

    public static final Function<String, ?> LITERAL =
            text -> {
                try {
                    return newLiteral.invoke(text);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };

    public static final Function<String, ?> TRANSLATABLE =
            key -> {
                try {
                    return newTranslatable.invoke(key);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };

    // spotless:off
    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try { // Component.literal(String)
            if (Constraint.builder().max(MinecraftVersions.V8_9).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.util.ChatComponentText");
                newLiteral = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V9)
                    .max(MinecraftVersions.V13_2).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.util.text.TextComponentString");
                newLiteral = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V14)
                    .max(MinecraftVersions.V16_5).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.util.text.StringTextComponent");
                newLiteral = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V17)
                    .max(MinecraftVersions.V18_2).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.network.chat.TextComponent");
                newLiteral = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .mappings(Mappings.SEARGE)
                    .min(MinecraftVersions.V19)
                    .max(MinecraftVersions.V20_4).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.network.chat.Component");
                Class<?> rType = Class.forName("net.minecraft.network.chat.MutableComponent");
                newLiteral = lookup.findStatic(clazz, "m_237113_", MethodType.methodType(rType, String.class));
            } else if (Constraint.builder()
                    .mappings(Mappings.MOJANG)
                    .min(MinecraftVersions.V19).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.network.chat.Component");
                Class<?> rType = Class.forName("net.minecraft.network.chat.MutableComponent");
                newLiteral = lookup.findStatic(clazz, "literal", MethodType.methodType(rType, String.class));
            } else {
                throw new ClassNotFoundException("No matching version for Component.literal");
            }
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            PCF.logger.error("Failed to initialize Component.literal function", e);
        }

        try { // Component.translatable(String)
            if (Constraint.builder().max(MinecraftVersions.V8_9).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.util.ChatComponentTranslation");
                newTranslatable = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class, Object[].class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V9)
                    .max(MinecraftVersions.V13_2).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.util.text.TextComponentTranslation");
                newTranslatable = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class, Object[].class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V14)
                    .max(MinecraftVersions.V16_5).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.util.text.TranslationTextComponent");
                newTranslatable = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class, Object[].class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V17)
                    .max(MinecraftVersions.V18_2).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.network.chat.TranslatableComponent");
                newTranslatable = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .mappings(Mappings.SEARGE)
                    .min(MinecraftVersions.V19)
                    .max(MinecraftVersions.V20_4).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.network.chat.Component");
                Class<?> rType = Class.forName("net.minecraft.network.chat.MutableComponent");
                newTranslatable = lookup.findStatic(clazz, "m_237115_", MethodType.methodType(rType, String.class));
            } else if (Constraint.builder()
                    .mappings(Mappings.MOJANG)
                    .min(MinecraftVersions.V19).build().result()) {
                Class<?> clazz = Class.forName("net.minecraft.network.chat.Component");
                Class<?> rType = Class.forName("net.minecraft.network.chat.MutableComponent");
                newTranslatable = lookup.findStatic(clazz, "translatable", MethodType.methodType(rType, String.class));
            } else {
                throw new ClassNotFoundException("No matching version for Component.translatable");
            }
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            PCF.logger.error("Failed to initialize Component.translatable function", e);
        }
    }
    // spotless:on

    @SuppressWarnings("unchecked")
    public static <T> T literal(String text) {
        return (T) LITERAL.apply(text);
    }

    @SuppressWarnings("unchecked")
    public static <T> T translatable(String key) {
        return (T) TRANSLATABLE.apply(key);
    }
}
