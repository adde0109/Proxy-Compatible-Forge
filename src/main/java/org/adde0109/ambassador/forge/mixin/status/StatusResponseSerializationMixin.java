package org.adde0109.ambassador.forge.mixin.status;

import com.google.gson.JsonObject;
import net.minecraft.network.ServerStatusResponse;
import org.adde0109.ambassador.forge.main;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.StandardCharsets;


@Mixin(ServerStatusResponse.Serializer.class)
public class StatusResponseSerializationMixin {

    @Inject(method = "serialize", at = @At("RETURN"), cancellable = true)
    private void lastSerialization(CallbackInfoReturnable<JsonObject> cir) {
       JsonObject jsonObject = cir.getReturnValue();
       jsonObject.remove("forgeData");

       if(!main.dataBuilt)
         main.buildData();

      jsonObject.add("modinfo",main.serializeJson(new String(main.parts.get(main.partNrToSend-1), StandardCharsets.ISO_8859_1),
              String.valueOf(main.partNrToSend) + "-" + String.valueOf(main.parts.size())+ main.packetSplitters));

      LogManager.getLogger().warn("Sent " + String.valueOf(main.partNrToSend) + " out of " + String.valueOf(main.parts.size()));
      LogManager.getLogger().warn("First byte: " + String.valueOf(main.parts.get(main.partNrToSend-1)[0]));

      LogManager.getLogger().warn(String.valueOf((new String(main.parts.get(main.partNrToSend-1), StandardCharsets.ISO_8859_1).getBytes(StandardCharsets.ISO_8859_1)[14])) + " Should be " + String.valueOf(main.parts.get(main.partNrToSend-1)[14]));
      LogManager.getLogger().warn(String.valueOf((new String(main.parts.get(main.partNrToSend-1), StandardCharsets.ISO_8859_1).getBytes(StandardCharsets.ISO_8859_1)[15])) + " Should be " + String.valueOf(main.parts.get(main.partNrToSend-1)[15]));
      LogManager.getLogger().warn(String.valueOf((new String(main.parts.get(main.partNrToSend-1), StandardCharsets.ISO_8859_1).getBytes(StandardCharsets.ISO_8859_1)[16])) + " Should be " + String.valueOf(main.parts.get(main.partNrToSend-1)[16]));
      LogManager.getLogger().warn(String.valueOf((new String(main.parts.get(main.partNrToSend-1), StandardCharsets.ISO_8859_1).getBytes(StandardCharsets.ISO_8859_1)[17])) + " Should be " + String.valueOf(main.parts.get(main.partNrToSend-1)[17]));

      main.partNrToSend = (main.partNrToSend >= main.parts.size()) ? 1 : main.partNrToSend + 1;




      cir.setReturnValue(jsonObject);
    }


}
