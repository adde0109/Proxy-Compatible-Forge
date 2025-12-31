package org.adde0109.pcf.forwarding.modern;

import static org.adde0109.pcf.forwarding.modern.ReflectionUtils.getId;
import static org.adde0109.pcf.forwarding.modern.ReflectionUtils.getName;

import com.mojang.authlib.GameProfile;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A record that holds a name and an ID
 *
 * @param name the player name
 * @param id the player UUID
 */
public record NameAndId(@NotNull String name, @NotNull UUID id) {
    public NameAndId(final @NotNull GameProfile profile) {
        this(getName(profile), getId(profile));
    }
}
