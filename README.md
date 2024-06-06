# Modern Forwarding
This mod brings modern forwarding to Forge servers (See: https://docs.papermc.io/velocity/player-information-forwarding)

# Command Wrapping fix
This mod also brings command wrapping which fixes this Velocity error: `io.netty.handler.codec.CorruptedFrameException: Error decoding class com.velocitypowered.proxy.protocol.packet.AvailableCommandsPacket`
## How to get started:
1. Download and install this as a mod to your Forge server. (Jars can be found in the releases tab.)
2. Start the server.
3. Close the server and open "pcf-common.toml" in the config folder and put your forwarding secret in the "forwardingSecret" field.
4. In "server.properties" make sure online-mode is set to false.
5. You are now ready to start the server and connect to it with Velocity!

Note: Latest version (> 1.1.7) of Ambassador doesn't require this mod anymore but you can still use it if you want modern forwarding.
