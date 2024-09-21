package org.adde0109.pcf.mixin.command;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.adde0109.pcf.command.IMixinNodeStub;
import org.adde0109.pcf.command.IMixinWrappableEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientboundCommandsPacket.Entry.class)
public class WrappableEntryMixin implements IMixinWrappableEntry {


  @Shadow
  @Final
  ClientboundCommandsPacket.NodeStub stub;

  @Shadow
  @Final
  int flags;

  @Shadow
  @Final
  int redirect;

  @Shadow
  @Final
  int[] children;

  public void wrapAndWrite(FriendlyByteBuf byteBuf) {
    byteBuf.writeByte(this.flags);
    byteBuf.writeVarIntArray(this.children);
    if ((this.flags & 8) != 0) {
      byteBuf.writeVarInt(this.redirect);
    }

    if (this.stub != null) {
      if ((Object) this.stub instanceof IMixinNodeStub) {
        ((IMixinNodeStub) (Object)this.stub).wrapAndWrite(byteBuf);
      } else
        this.stub.write(byteBuf);
    }
  }

}
