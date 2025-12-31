package org.adde0109.pcf.forwarding.modern;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import net.minecraft.server.MinecraftServer;

import org.adde0109.pcf.PCF;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.UUID;

public final class ReflectionUtils {
    private ReflectionUtils() {}

    // com.mojang:authlib:7.0.0 or newer
    static final Constraint V21_9 = Constraint.builder().min(MinecraftVersions.V21_9).build();

    private static final MethodHandle nameHandle;
    private static final MethodHandle idHandle;
    private static final MethodHandle profilePropertiesHandle;

    static {
        if (V21_9.result()) {
            nameHandle = null;
            idHandle = null;
            profilePropertiesHandle = null;
        } else {
            try {
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                //noinspection JavaLangInvokeHandleSignature
                nameHandle =
                        lookup.findVirtual(
                                GameProfile.class, "getName", MethodType.methodType(String.class));
                //noinspection JavaLangInvokeHandleSignature
                idHandle =
                        lookup.findVirtual(
                                GameProfile.class, "getId", MethodType.methodType(UUID.class));
                //noinspection JavaLangInvokeHandleSignature
                profilePropertiesHandle =
                        lookup.findVirtual(
                                GameProfile.class,
                                "getProperties",
                                MethodType.methodType(PropertyMap.class));
            } catch (final NoSuchMethodException | IllegalAccessException e) {
                throw new IllegalStateException(
                        "Failed to initialize GameProfile method handles", e);
            }
        }
    }

    /**
     * Gets the properties from the given GameProfile - 1.21.8 and older
     *
     * @param profile the profile
     * @return the properties
     */
    static @NotNull PropertyMap getProperties(final @NotNull GameProfile profile) {
        try {
            return (PropertyMap) profilePropertiesHandle.invokeExact(profile);
        } catch (final Throwable e) {
            throw new IllegalStateException("Failed to get properties from GameProfile", e);
        }
    }

    /**
     * Gets the name from the given GameProfile
     *
     * @param profile the profile
     * @return the name
     */
    static @NotNull String getName(final @NotNull GameProfile profile) {
        if (V21_9.result()) {
            return profile.name();
        } else {
            try {
                return (String) nameHandle.invokeExact(profile);
            } catch (final Throwable e) {
                throw new IllegalStateException("Failed to get name from GameProfile", e);
            }
        }
    }

    /**
     * Gets the id from the given GameProfile
     *
     * @param profile the profile
     * @return the id
     */
    static @NotNull UUID getId(final @NotNull GameProfile profile) {
        if (V21_9.result()) {
            return profile.id();
        } else {
            try {
                return (UUID) idHandle.invokeExact(profile);
            } catch (final Throwable e) {
                throw new IllegalStateException("Failed to get id from GameProfile", e);
            }
        }
    }

    private static final Constraint IS_19_X_19_2 =
            Constraint.builder().min(MinecraftVersions.V19).max(MinecraftVersions.V19_2).build();

    private static final MethodHandle ENFORCE_SECURE_PROFILE;

    static {
        MethodHandle enforceSecureProfileHandle = null;
        if (IS_19_X_19_2.result()) {
            try {
                Class<MinecraftServer> minecraftServerClass = MinecraftServer.class;
                //noinspection JavaLangInvokeHandleSignature
                enforceSecureProfileHandle =
                        MethodHandles.lookup()
                                .findVirtual(
                                        minecraftServerClass,
                                        "m_214005_", // enforceSecureProfile
                                        MethodType.methodType(boolean.class));
            } catch (NoSuchMethodException | IllegalAccessException e) {
                PCF.logger.error(
                        "Failed to get MethodHandle for MinecraftServer.enforceSecureProfile", e);
            }
        }
        ENFORCE_SECURE_PROFILE = enforceSecureProfileHandle;
    }

    static boolean enforceSecureProfile() {
        if (ENFORCE_SECURE_PROFILE == null) {
            return false;
        }
        try {
            return (boolean)
                    ENFORCE_SECURE_PROFILE.invokeExact(
                            (MinecraftServer) MetaAPI.instance().server());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
