package org.adde0109.pcf;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;

@Mod("proxy-compatible-forge")
public class Initializer {

public static ModernForwarding modernForwardingInstance;

public static final Config config;

  public Initializer() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,configSpec);

    //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
    ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

    MinecraftForge.EVENT_BUS.addListener(this::serverAbutToStart);
  }



  public void serverAbutToStart(FMLServerAboutToStartEvent event) {
    String forwardingSecret = config.forwardingSecret.get();
    if(!Objects.equals(forwardingSecret, "")) {
      modernForwardingInstance = new ModernForwarding(forwardingSecret);
    }
  }


  static final ForgeConfigSpec configSpec;
  static {
    final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Initializer.Config::new);
    configSpec = specPair.getRight();
    config = specPair.getLeft();
  }

  public static class Config {
    public final ForgeConfigSpec.ConfigValue<? extends String> forwardingSecret;

    Config(ForgeConfigSpec.Builder builder)
    {
      builder.comment("Modern Forwarding Settings")
              .push("modernForwarding");

      forwardingSecret = builder
              .define("forwardingSecret", "");

      builder.pop();
    }

  }





}