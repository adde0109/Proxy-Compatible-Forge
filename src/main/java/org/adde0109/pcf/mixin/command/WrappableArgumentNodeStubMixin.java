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

import io.netty.buffer.Unpooled;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.adde0109.pcf.Initializer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(targets = "net.minecraft.network.protocol.game.ClientboundCommandsPacket$ArgumentNodeStub")
public class WrappableArgumentNodeStubMixin {
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

  /**
   * @author Daniel Voort.
   * @reason This is easier than injecting and returning before anything is written. There are viable alternatives
   *  available, but this is just the most straightforward and most development-time efficient. It is highly unlikely
   *  for other mods to try to mixin this particular function.
   */
  @Overwrite
  public void write(FriendlyByteBuf buffer) {
    buffer.writeUtf(this.id);

    var typeInfo = argumentType.type();
    var identifier = ForgeRegistries.COMMAND_ARGUMENT_TYPES.getKey(typeInfo);
    var id = BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getId(typeInfo);

    if (identifier != null && Initializer.integratedArgumentTypes.contains(identifier.toString())) {
      buffer.writeVarInt(id);
      ((ArgumentTypeInfo) typeInfo).serializeToNetwork(argumentType, buffer);
    } else {
      buffer.writeVarInt(MOD_ARGUMENT_INDICATOR);
      buffer.writeVarInt(id);

      FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
      ((ArgumentTypeInfo) typeInfo).serializeToNetwork(argumentType, extraData);

      buffer.writeVarInt(extraData.readableBytes());
      buffer.writeBytes(extraData);

      extraData.release();
    }

    if (suggestionId != null)
      buffer.writeResourceLocation(suggestionId);
  }
}
