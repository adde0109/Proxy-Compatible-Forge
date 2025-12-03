package org.adde0109.pcf.mixin.v1_20_2.neoforge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings({"unused", "RedundantThrows"})
@AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V20_2))
@Mixin(value = ServerLoginPacketListenerImpl.class, priority = 250)
public class ServerLoginPacketListenerPlaceholderMixin {
    @Unique
    void arclight$preLogin() throws Exception {}
}
