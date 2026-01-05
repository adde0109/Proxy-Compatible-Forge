package org.adde0109.pcf.mixin.v17_1.forge.crossstitch;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import io.netty.buffer.ByteBuf;

import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.crossstitch.EntryBridge;
import org.adde0109.pcf.crossstitch.SerializerBridge;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@AConstraint(
        mappings = Mappings.SEARGE,
        version = @Versions(min = MinecraftVersion.V17, max = MinecraftVersion.V18_2))
@Mixin(ArgumentTypes.Entry.class)
public class ArgumentTypesEntryMixin<T extends ArgumentType<?>>
        implements EntryBridge, SerializerBridge {
    // spotless:off
    @Shadow @Final public ResourceLocation name;
    @Shadow @Final public ArgumentSerializer<T> serializer;
    // spotless:on

    @Override
    public @NotNull String bridge$identifier() {
        return this.name.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void bridge$serializeToNetwork(final @NotNull Object argument, @NotNull ByteBuf buffer) {
        if (buffer instanceof dev.neuralnexus.taterapi.network.FriendlyByteBuf fByteBuf) {
            buffer = fByteBuf.unwrap();
        }
        final FriendlyByteBuf buf;
        if (buffer instanceof FriendlyByteBuf friendlyByteBuf) {
            buf = friendlyByteBuf;
        } else {
            buf = new FriendlyByteBuf(buffer);
        }
        this.serializer.serializeToNetwork((T) argument, buf);
    }
}
