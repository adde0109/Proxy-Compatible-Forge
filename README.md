# Proxy Compatible Forge

[![Github](https://img.shields.io/github/stars/adde0109/Proxy-Compatible-Forge)](https://github.com/adde0109/Proxy-Compatible-Forge)
[![Github Issues](https://img.shields.io/github/issues/adde0109/Proxy-Compatible-Forge?label=Issues)](https://github.com/adde0109/Proxy-Compatible-Forge/issues)
[![Discord](https://img.shields.io/discord/1064999648101671003?color=7289da&logo=discord&logoColor=white)](https://discord.gg/Vusz9pBNyJ)

[![Github Releases](https://img.shields.io/github/downloads/adde0109/Proxy-Compatible-Forge/total?label=Github&logo=github&color=181717)](https://github.com/adde0109/Proxy-Compatible-Forge/releases)
[![Modrinth](https://img.shields.io/modrinth/dt/proxy-compatible-forge?label=Modrinth&logo=modrinth&color=00AF5C)](https://modrinth.com/mod/proxy-compatible-forge)

Special thanks to [FabricProxy-Lite](<https://github.com/OKTW-Network/FabricProxy-Lite>) and
[CrossStitch](<https://github.com/VelocityPowered/CrossStitch>) for spearheading in the modded proxy space. We've done
our fair share of work porting things and tailoring them to a Neo/Forge environment, but nonetheless we stand on the shoulders of giants.

This mod brings Velocity's [modern forwarding](<https://docs.papermc.io/velocity/player-information-forwarding>) to Neo/Forge servers

## Features

### Supported Versions/Platforms

- Forge versions 1.14-1.21.10
  - [MixinBootstrap](https://modrinth.com/mod/mixinbootstrap) is required on Forge 1.14.x - 1.15.1
- NeoForge versions 1.20.1-1.21.10
- SpongeForge/SpongeNeo
  - PCF shouldn't be needed, as Sponge supports legacy+modern forwarding and command argument wrapping
  - However, if Forgified Fabric API is installed, you may need to use PCF and disable Sponge's forwarding
- Bukkit+Neo/Forge Hybrid servers
  - Use the Hybrid's built-in forwarding support when possible, or PCF if their implementation is incompatible

See the [Compatibility](https://github.com/adde0109/Proxy-Compatible-Forge/blob/main/docs/Compatibility.md) page for more details on supported platforms and modpacks.

### Player Info Forwarding

As mentioned above, PCF implements Velocity's "Modern" forwarding protocol, allowing you to secure modded servers behind proxies.

Currently, PCF only supports the default modern forwarding (v1) variant of the protocol, but with the recent refactor cleaning things up I'd like to add the other three when I have the chance.

### Modded Command Argument Wrapping, aka [CrossStitch](<https://github.com/VelocityPowered/CrossStitch>)

This resolves errors such as:
```
io.netty.handler.codec.CorruptedFrameException: Error decoding class com.velocitypowered.proxy.protocol.packet.AvailableCommandsPacket
```

PCF ports this Fabric mod's ability to wrap modded command arguments, allowing them to be sent through Velocity without
there needing to be a custom packet deserializer for each and every command argument mods add.

In some rare cases mods will register their command arguments under the `minecraft` namespace or make modifications to
vanilla arguments, bypassing PCF's argument wrapper. In such cases, you can add the custom argument's ID to PCF's
`forceWrappedArguments` setting to force PCF to wrap the argument.

In situations where mods inject and register their arguments before Vanilla does, offsetting all the argument ID values,
you can enable the `forceWrapVanillaArguments` setting to force-wrap the `minecraft` and `brigadier` namespaces.
This is more of a band-aid solution as the offset argument IDs cannot be read by Velocity, and if the argument IDs on
the client have the same offset Velocity commands cannot be read by the client, preventing connections entirely unless
you disable all commands on the proxy via permissions or by removing plugins that add commands.

The easiest way to tell if this is happening is to enable PCF's debug logging in the config and check to see if
`brigadier:boolean` has an argument ID other than `0`.

A common fix for this is for the mod in question to move their registration mixin to `RETURN`, and to ensure that their
argument's registration specifies their modid as the namespace.

If you find any wild args, please open an issue so we can add them to the default config,
or so we can use the information provided to write up a PR for the mod causing the issue. 

## How to Get Started

### Note Regarding Forge 1.13-1.20.1

If you wish to host modern Forge server (1.13-1.20.1) behind a Velocity proxy, check out Ambassador: <https://modrinth.com/plugin/ambassador>

*Note: Version 1.2.0-beta or higher of Ambassador doesn't require this mod anymore, but you can still use it if you want modern forwarding.*

### Installation

The following assumes you've already [configured a Velocity proxy](<https://docs.papermc.io/velocity/getting-started/>) and have a functional setup.

1. Download this mod and place it in your Neo/Forge server's `mods` folder (Jars can be found  on [Modrinth](<https://modrinth.com/mod/proxy-compatible-forge/versions>) or in the releases tab).
2. Start the Neo/Forge server to generate the default config file.
3. Stop the Neo/Forge server.
4. Open `proxy-compatible-forge.toml` in the `config` folder and put your forwarding secret in the `secret` config field.
5. In `server.properties` make sure `online-mode` is set to `false`.
6. You are now ready to start the server and connect to it with Velocity!

### Configuration

The config is located under `config/proxy-compatible-forge.toml` and has the following options:

| Setting Group | Setting Name                | Default Value | Description                                                                                                                                        |
|---------------|-----------------------------|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| `forwarding`  | `enabled`                   | `true`        | Enable or disable player info forwarding. Changing this setting requires a server restart.                                                         |
| `forwarding`  | `mode`                      | `\"MODERN\"`  | The type of forwarding to use.                                                                                                                     |
| `forwarding`  | `secret`                    | `\"\"`        | The secret used to verify the player's connection is coming from a trusted proxy. PCF will only handle argument wrapping if this setting is blank. |
| `crossStitch` | `enabled`                   | `true`        | Enable or disable CrossStitch support. Changing this setting requires a server restart.                                                            |
| `crossStitch` | `forceWrappedArguments`     | `[]`          | Add any incompatible modded or vanilla command argument types here.                                                                                |
| `crossStitch` | `forceWrapVanillaArguments` | `false`       | Force wrap vanilla command argument types. Useful for when the above setting gets a bit excessive.                                                 |
| `debug`       | `enabled`                   | `false`       | Enable or disable debug logging.                                                                                                                   |
| `debug`       | `disabledMixins`            | `[]`          | List of mixins to disable. Use the Mixin's name and prefix it with it's partial or full package name.                                              |

### Common Issues

#### Too Many Channels

**Client Error:**
```
Invalid payload REGISTER!
```

**Velocity:**

Velocity Fix: Add `-Dvelocity.max-known-packs=#` to the Velocity's startup arguments,
where `#` is a number that is 64 + number-of-mods*1.5 (round up).

Eg: For 40 mods, use `64 + 40*1.5 = 124`, so `-Dvelocity.max-known-packs=124`

**Paper Server Error:**
```
[00:00:00 ERROR]: Couldn't register custom payload
java.lang.IllegalStateException: Cannot register channel 'modid:channel'. Too many channels registered!
```

Paper Fix: Add `-Dpaper.disableChannelLimit=true` to the Paper server's startup arguments

Notes: Due to the amount of channels and mods at play, textures/items may be mismatched on the client when joining a Vanilla server.

## Building the Project

1. Clone the repository. `git clone https://github.com/adde0109/Proxy-Compatible-Forge.git`

2. Run `./gradlew buildAllTheStuffNowBcGradleIsDumb` in the root directory of the project. (I love gradle so much)

    Note: You only need to run this once, after that you can just run `./gradlew build`. On future builds gradle will
    actually wait for the subprojects to build before merging the jars (usually). The workaround is needed due to how
    gradle's dependency graph works, causing it to look ahead and have a fit since the built Jars don't exist yet.

3. Run `./gradlew build` in the root directory of the project.

4. The jar will be in `build/libs/`
