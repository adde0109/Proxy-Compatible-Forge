//Contains code from: https://github.com/OKTW-Network/FabricProxy-Lite/blob/master/src/main/java/one/oktw/VelocityLib.java
package org.adde0109.ambassador.forge;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class modernForwarding {

  private static final int SUPPORTED_FORWARDING_VERSION = 1;

  public static boolean validate(PacketBuffer buffer) {
    final byte[] signature = new byte[32];
    buffer.readBytes(signature);

    final byte[] data = new byte[buffer.readableBytes()];
    buffer.getBytes(buffer.readerIndex(), data);

    try {
      final Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec("OMnLBWOdlQck".getBytes(), "HmacSHA256"));
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

  public static PropertyMap readProperties(PacketBuffer buf) {
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
