package org.adde0109.pcf.mixin.registry;

import net.minecraftforge.registries.RegistryBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RegistryBuilder.class, remap = false)
public class RegistryBuilderMixin {

  @Shadow
  private boolean saveToDisc = true;
  @Shadow
  private boolean sync = true;
  @Inject(method = "getSaveToDisc", at = @At("HEAD"), cancellable = true)
  private void onGetSaveToDisk(CallbackInfoReturnable<Boolean> cir) {
    cir.setReturnValue(saveToDisc || sync);
    cir.cancel();
  }
}
