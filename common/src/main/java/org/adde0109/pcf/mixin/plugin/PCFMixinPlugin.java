package org.adde0109.pcf.mixin.plugin;

import dev.neuralnexus.taterapi.muxins.Muxins;

import org.adde0109.pcf.PCF;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/** A mixin plugin for PCF. */
@SuppressWarnings("unused")
public class PCFMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    // TODO: Conditionally apply mixins based on enable configs
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // TODO: Undo this
        // PCF.forceLoadConfig();
        PCF.Debug debug = new PCF.Debug(true, List.of()); // PCF.instance().debug();
        return Muxins.shouldApplyMixin(mixinClassName, debug.disabledMixins(), debug.enabled());
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(
            String targetClassName,
            ClassNode targetClass,
            String mixinClassName,
            IMixinInfo mixinInfo) {}

    @Override
    public void postApply(
            String targetClassName,
            ClassNode targetClass,
            String mixinClassName,
            IMixinInfo mixinInfo) {}
}
