package org.adde0109.pcf.mixin.command;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.adde0109.pcf.command.IMixinWrappableCommandPacket;
import org.adde0109.pcf.command.IMixinWrappableEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ClientboundCommandsPacket.class)
public class WrappableCommandsPacketMixin implements IMixinWrappableCommandPacket {

  @Shadow
  @Final
  private int rootIndex;

  @Shadow
  @Final
  private List<ClientboundCommandsPacket.Entry> entries;

  public void wrapAndWrite(FriendlyByteBuf byteBuf) {
    byteBuf.writeCollection(this.entries, (p_237642_, p_237643_) -> {
      ((IMixinWrappableEntry) (Object) p_237643_).wrapAndWrite(p_237642_);
    });
    byteBuf.writeVarInt(this.rootIndex);
  }


}
