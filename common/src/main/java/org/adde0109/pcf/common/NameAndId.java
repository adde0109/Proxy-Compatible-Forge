package org.adde0109.pcf.common;

import com.mojang.authlib.GameProfile;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import java.lang.reflect.Method;
import java.util.UUID;

public record NameAndId(String name, UUID id) {
    private static final boolean isAtLeast21_9 =
            MetaAPI.instance().version().isAtLeast(MinecraftVersions.V21_9);
    private static final Method nameMethod;
    private static final Method idMethod;

    static {
        if (isAtLeast21_9) {
            try {
                nameMethod = GameProfile.class.getMethod("getName");
                idMethod = GameProfile.class.getMethod("getId");
            } catch (NoSuchMethodException e) {
                throw new ExceptionInInitializerError(e);
            }
        } else {
            nameMethod = null;
            idMethod = null;
        }
    }

    public NameAndId(GameProfile profile) {
        this(extractName(profile), extractId(profile));
    }

    private static String extractName(GameProfile profile) {
        if (isAtLeast21_9) {
            try {
                return (String) nameMethod.invoke(profile);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to get name from GameProfile", e);
            }
        } else {
            return profile.name();
        }
    }

    private static UUID extractId(GameProfile profile) {
        if (isAtLeast21_9) {
            try {
                return (UUID) idMethod.invoke(profile);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to get id from GameProfile", e);
            }
        } else {
            return profile.id();
        }
    }
}
