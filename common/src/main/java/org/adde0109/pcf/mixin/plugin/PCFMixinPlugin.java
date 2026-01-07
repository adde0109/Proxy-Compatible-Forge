package org.adde0109.pcf.mixin.plugin;

import static org.adde0109.pcf.forwarding.modern.VelocityProxy.Version.MODERN_FORWARDING_WITH_KEY;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.Version.MODERN_FORWARDING_WITH_KEY_V2;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.Version.NO_OVERRIDE;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.muxins.Muxins;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.forwarding.Mode;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/** A mixin plugin for PCF. */
@SuppressWarnings("unused")
public final class PCFMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(final @NonNull String mixinPackage) {
        try {
            PCF.forceLoadConfig();
            Muxins.bootstrap(mixinPackage, PCF.instance().debug().enabled());
        } catch (final Exception e) {
            PCF.logger.error("Error during Muxins bootstrap:");
            e.printStackTrace();
        }
    }

    @Override
    public @Nullable String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(
            final @NonNull String targetClassName, final @NonNull String mixinClassName) {
        try {
            PCF.forceLoadConfig();
            final PCF.Forwarding forwarding = PCF.instance().forwarding();
            final PCF.CrossStitch crossStitch = PCF.instance().crossStitch();
            final PCF.Debug debug = PCF.instance().debug();
            final PCF.Advanced advanced = PCF.instance().advanced();
            return shouldApplyMixin(mixinClassName, forwarding, crossStitch, advanced)
                    && Muxins.shouldApplyMixin(
                            mixinClassName, debug.disabledMixins(), debug.enabled());
        } catch (final Exception e) {
            PCF.logger.error("Error while checking whether to apply mixin: " + mixinClassName);
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Determines whether a mixin should be applied based on the PCF configuration. <br>
     * <br>
     * If the version is set to 1.19 and the overridden modern forwarding version is not
     * MODERN_FORWARDING_WITH_KEY, mixins related to chat-signing will be disabled. <br>
     * <br>
     * If the version is set to 1.19.1 or 1.19.2 and the overridden modern forwarding version is not
     * MODERN_FORWARDING_WITH_KEY_V2, mixins related to chat-signing will be disabled.
     *
     * @param m the mixin class name
     * @param forwarding PCF's forwarding configuration
     * @param crossStitch PCF's CrossStitch configuration
     * @param advanced PCF's Advanced configuration
     * @return true if the mixin should be applied, false otherwise
     */
    private static boolean shouldApplyMixin(
            final @NonNull String m,
            final PCF.@NonNull Forwarding forwarding,
            final PCF.@NonNull CrossStitch crossStitch,
            final PCF.@NonNull Advanced advanced) {
        if (!forwarding.enabled() && m.contains(".forwarding.")) {
            PCF.logger.debug("Skipping mixin " + m + " because forwarding is disabled.");
            return false;
        }
        if (forwarding.mode() != Mode.MODERN && m.contains(".forwarding.modern.")) {
            PCF.logger.debug("Skipping mixin " + m + " because forwarding mode is not MODERN.");
            return false;
        }
        if (advanced.modernForwardingVersion() != NO_OVERRIDE
                && Constraint.builder().version(MinecraftVersions.V19).result()
                && advanced.modernForwardingVersion() != MODERN_FORWARDING_WITH_KEY
                && m.endsWith("KeyV1Mixin")) {
            PCF.logger.debug(
                    "Skipping mixin "
                            + m
                            + " because overridden modern forwarding version is not MODERN_FORWARDING_WITH_KEY.");
            return false;
        }
        if (advanced.modernForwardingVersion() != NO_OVERRIDE
                && Constraint.range(MinecraftVersions.V19_1, MinecraftVersions.V19_2).result()
                && advanced.modernForwardingVersion() != MODERN_FORWARDING_WITH_KEY_V2
                && (m.endsWith("KeyV2Mixin")
                        || m.endsWith("LastSeenMessagesValidatorMixin")
                        || m.endsWith("PlayerChatMessageMixin")
                        || m.endsWith("SignedMessageChainMixin"))) {
            PCF.logger.debug(
                    "Skipping mixin "
                            + m
                            + " because overridden modern forwarding version is not MODERN_FORWARDING_WITH_KEY_V2.");
            return false;
        }

        if (!crossStitch.enabled() && m.contains(".crossstitch.")) {
            PCF.logger.debug("Skipping mixin " + m + " because CrossStitch is disabled.");
            return false;
        }
        return true;
    }

    @Override
    public void acceptTargets(
            final Set<@NonNull String> myTargets, final Set<@NonNull String> otherTargets) {}

    @Override
    public @Nullable List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(
            final @NonNull String targetClassName,
            final @NonNull ClassNode targetClass,
            final @NonNull String mixinClassName,
            final @NonNull IMixinInfo mixinInfo) {}

    @Override
    public void postApply(
            final @NonNull String targetClassName,
            final @NonNull ClassNode targetClass,
            final @NonNull String mixinClassName,
            final @NonNull IMixinInfo mixinInfo) {}
}
