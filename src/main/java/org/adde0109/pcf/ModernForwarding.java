//Contains code from: https://github.com/OKTW-Network/FabricProxy-Lite/blob/master/src/main/java/one/oktw/VelocityLib.java
package org.adde0109.pcf;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ModernForwarding {

  private static final int SUPPORTED_FORWARDING_VERSION = 1;

  private final String forwardingSecret;

  ModernForwarding(String forwardingSecret) {
    this.forwardingSecret = forwardingSecret;
  }


  @Nullable
  public GameProfile handleForwardingPacket(CCustomPayloadLoginPacket packet) {
    PacketBuffer data = packet.getInternalData();
    if(data != null) {
      LogManager.getLogger().debug("Received forwarding packet!");

      if(validate(data)) {
        LogManager.getLogger().debug("Player-data validated!");
        data.readUtf(); //Never used
        GameProfile forwardedProfile = new GameProfile(data.readUUID(), data.readUtf());

        return forwardedProfile;
      }
    }
    return null;
  }

  public boolean validate(PacketBuffer buffer) {
    final byte[] signature = new byte[32];
    buffer.readBytes(signature);

    final byte[] data = new byte[buffer.readableBytes()];
    buffer.getBytes(buffer.readerIndex(), data);

    try {
      final Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(forwardingSecret.getBytes(), "HmacSHA256"));
      final byte[] mySignature = mac.doFinal(data);
      if (!MessageDigest.isEqual(signature, mySignature)) {
        return false;
      }
    } catch (final InvalidKeyException | NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
    int version = buffer.readVarInt();
    if (version != SUPPORTED_FORWARDING_VERSION) {
      throw new IllegalStateException("Unsupported forwarding version " + version + ", wanted " + SUPPORTED_FORWARDING_VERSION);
    }

    return true;
  }

  public PropertyMap readProperties(PacketBuffer buf) {
    PropertyMap properties = new PropertyMap();
    int size = buf.readVarInt();
    for (int i = 0; i < size; i++) {
      String name = buf.readUtf();
      String value = buf.readUtf();
      String signature = "";
      boolean hasSignature = buf.readBoolean();
      if (hasSignature) {
        signature = buf.readUtf();
      }
      properties.put(name,new Property(name, value, signature));
    }
    return properties;
  }

}
