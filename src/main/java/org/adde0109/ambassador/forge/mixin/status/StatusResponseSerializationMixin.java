package org.adde0109.ambassador.forge.mixin.status;

import com.google.gson.JsonObject;
import net.minecraft.network.ServerStatusResponse;
import org.adde0109.ambassador.forge.handshakeDataTransmitter;
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

        if(handshakeDataTransmitter.storedHandshakeData == null)
          handshakeDataTransmitter.storedHandshakeData = new handshakeDataTransmitter.handshakeData();

        handshakeDataTransmitter.handshakeData data = handshakeDataTransmitter.storedHandshakeData;
        jsonObject.add("modinfo", handshakeDataTransmitter.serializeJson(new String(data.parts.get(handshakeDataTransmitter.partNrToSend-1), StandardCharsets.ISO_8859_1),
              String.valueOf(handshakeDataTransmitter.partNrToSend) + "-" + String.valueOf(data.parts.size())+ data.packetSplitters));

        handshakeDataTransmitter.partNrToSend = (handshakeDataTransmitter.partNrToSend >= data.parts.size()) ? 1 : handshakeDataTransmitter.partNrToSend + 1;


        cir.setReturnValue(jsonObject);
    }


}
