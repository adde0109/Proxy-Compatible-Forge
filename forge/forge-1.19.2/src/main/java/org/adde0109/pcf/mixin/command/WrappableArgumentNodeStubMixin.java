package org.adde0109.pcf.mixin.command;

/*
The MIT License (MIT)

        Copyright (c) 2020 Andrew Steinborn
        Copyright (c) 2020 Velocity Contributors

        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in
        all copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
        THE SOFTWARE.
*/

import com.mojang.brigadier.arguments.ArgumentType;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.adde0109.pcf.Initializer;
import org.adde0109.pcf.command.IMixinNodeStub;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(targets = "net.minecraft.network.protocol.game.ClientboundCommandsPacket$ArgumentNodeStub")
public class WrappableArgumentNodeStubMixin implements IMixinNodeStub {
  private static final int MOD_ARGUMENT_INDICATOR = -256;

  @Shadow
  @Final
  private ArgumentTypeInfo.Template<?> argumentType;

  @Shadow
  @Final
  private String id;

  @Shadow
  @Final
  private ResourceLocation suggestionId;

  public void wrapAndWrite(FriendlyByteBuf byteBuf) {
    byteBuf.writeUtf(this.id);
    wrapInVelocityModArgument(byteBuf, this.argumentType);
    if (this.suggestionId != null) {
      byteBuf.writeResourceLocation(this.suggestionId);
    }
  }

  private static <A extends ArgumentType<?>> void wrapInVelocityModArgument(FriendlyByteBuf buf, ArgumentTypeInfo.Template<A> properties) {
    wrapInVelocityModArgument(buf, properties.type(), properties);
  }

  private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void wrapInVelocityModArgument(FriendlyByteBuf buf, ArgumentTypeInfo<A, T> serializer, ArgumentTypeInfo.Template<A> properties) {
    ResourceLocation identifier = Registry.COMMAND_ARGUMENT_TYPE.getKey(properties.type());

    if (identifier != null && Initializer.integratedArgumentTypes.contains(identifier.toString())) {
      buf.writeVarInt(Registry.COMMAND_ARGUMENT_TYPE.getId(serializer));
      serializer.serializeToNetwork((T)properties, buf);
      return;
    }

    // Not a standard Minecraft argument type - so we need to wrap it
    serializeWrappedArgumentType(buf, properties.type(), properties);
  }

  private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void serializeWrappedArgumentType(FriendlyByteBuf packetByteBuf, ArgumentTypeInfo<A, T> serializer, ArgumentTypeInfo.Template<A> properties) {
    packetByteBuf.writeVarInt(MOD_ARGUMENT_INDICATOR);
    packetByteBuf.writeVarInt(Registry.COMMAND_ARGUMENT_TYPE.getId(serializer));

    FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
    serializer.serializeToNetwork((T) properties, extraData);

    packetByteBuf.writeVarInt(extraData.readableBytes());
    packetByteBuf.writeBytes(extraData);
  }
}
