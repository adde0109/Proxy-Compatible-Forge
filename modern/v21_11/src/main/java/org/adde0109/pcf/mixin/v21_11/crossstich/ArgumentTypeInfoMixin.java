package org.adde0109.pcf.mixin.v21_11.crossstich;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import io.netty.buffer.ByteBuf;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

import org.adde0109.pcf.crossstitch.SerializerBridge;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V19))
@Mixin(ArgumentTypeInfo.class)
public interface ArgumentTypeInfoMixin<
                A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>>
        extends SerializerBridge {
    // spotless:off
    @Shadow void shadow$serializeToNetwork(T template, FriendlyByteBuf buffer);
    // spotless:on

    @SuppressWarnings("unchecked")
    @Override
    default void bridge$serializeToNetwork(
            final @NonNull Object argument, @NonNull ByteBuf buffer) {
        if (buffer instanceof dev.neuralnexus.taterapi.network.FriendlyByteBuf fByteBuf) {
            buffer = fByteBuf.unwrap();
        }
        final FriendlyByteBuf buf;
        if (buffer instanceof FriendlyByteBuf friendlyByteBuf) {
            buf = friendlyByteBuf;
        } else {
            buf = new FriendlyByteBuf(buffer);
        }
        this.shadow$serializeToNetwork((T) argument, buf);
    }
}
