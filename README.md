# Proxy Compatible Forge

Special thanks to [FabricProxy-Lite](<https://github.com/OKTW-Network/FabricProxy-Lite>) and
[CrossStitch](<https://github.com/VelocityPowered/CrossStitch>) for spearheading in the modded proxy space. We've done
our fair share of work porting things and tailoring them to a Neo/Forge environment, but nonetheless we stand on the shoulders of giants.

This mod brings Velocity's [modern forwarding](<https://docs.papermc.io/velocity/player-information-forwarding>) to Neo/Forge servers

## Features

### Supported Versions/Platforms
- Forge versions 1.14-1.21.10
- NeoForge versions 1.20.1-1.21.10
- SpongeForge/SpongeNeo
  - Use Sponge's built-in forwarding support, then PCF just for wrapping command arguments
- Bukkit+Neo/Forge Hybrid servers
  - Use the Hybrid's built-in forwarding support when possible, or PCF if their implementation is incompatible

See the [Compatibility](https://github.com/adde0109/Proxy-Compatible-Forge/blob/main/docs/Compatibility.md) page for more details on supported platforms and modpacks.

### Player Info Forwarding

As mentioned above, PCF implements Velocity's "Modern" forwarding protocol, allowing you to secure modded servers behind proxies.

Currently, PCF only supports the default modern forwarding (v1) variant of the protocol, but with the recent refactor cleaning things up I'd like to add the other three when I have the chance.

### Modded Command Argument Wrapping, aka [CrossStitch](<https://github.com/VelocityPowered/CrossStitch>)

PCF ports this Fabric mod's ability to wrap modded command arguments, allowing them to be sent through Velocity without
there needing to be a custom packet deserializer for each and every command argument mods add.

<sub><sup>
Fun note: PCF also fixes Forge 1.14-1.15.2 so the `forge:enum` and `forge:modlist` command arguments are
serialized correctly. Only caveat being that `forge:enum` cannot send any extra data on those versions due to the
underlying vanilla registration system being brittle with generics.
</sup></sub>

In some rare cases mods will register their command arguments to the wrong registry or make modifications to vanilla
arguments, bypassing PCF's argument wrapper. In such cases, you can add the custom argument's ID to PCF's
`forceWrappedArguments` setting to force PCF to wrap the argument.

If you find any wild args, please open an issue so we can add them to the default config.

## How to Get Started

### Note Regarding Forge 1.13-1.20.1

If you wish to host modern Forge server (1.13-1.20.1) behind a Velocity proxy, check out Ambassador: <https://modrinth.com/plugin/ambassador>

*Note: Version 1.2.0-beta or higher of Ambassador doesn't require this mod anymore, but you can still use it if you want modern forwarding.*

### Installation

The following assumes you've already [configured a Velocity proxy](<https://docs.papermc.io/velocity/getting-started/>) and have a functional setup.

1. Download this mod and place it in your Neo/Forge server's `mods` folder (Jars can be found  on [Modrinth](<https://modrinth.com/mod/proxy-compatible-forge/versions>) or in the releases tab).
2. Start the server to generate the default config file.
3. Optionally stop the Neo/Forge server at this point.
4. Open `pcf-common.toml` in the `config` folder and put your forwarding secret in the `secret` config field.
5. In `server.properties` make sure `online-mode` is set to `false`.
6. You are now ready to start the server and connect to it with Velocity!

### Configuration

The config is located under `config/pcf-common.toml` and has the following options:

| Setting Group | Setting Name            | Default Value | Description                                                                                                                                        |
|---------------|-------------------------|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| `forwarding`  | `enabled`               | `true`        | Enable or disable player info forwarding. This setting requires a server restart.                                                                  |
| `forwarding`  | `mode`                  | `\"MODERN\"`  | The type of forwarding to use.                                                                                                                     |
| `forwarding`  | `secret`                | `\"\"`        | The secret used to verify the player's connection is coming from a trusted proxy. PCF will only handle argument wrapping if this setting is blank. |
| `crossStitch` | `enabled`               | `true`        | Enable or disable CrossStitch support. This setting requires a server restart.                                                                     |
| `crossStitch` | `forceWrappedArguments` | `[]`          | Add any incompatible modded or vanilla command argument types here.                                                                                |
| `debug`       | `enabled`               | `false`       | Enable or disable debug logging.                                                                                                                   |
| `debug`       | `disabledMixins`        | `[]`          | List of mixins to disable. Use the Mixin's name and prefix it with it's partial or full package name.                                              |

## Building the Project

1. Clone the repository. `git clone https://github.com/adde0109/Proxy-Compatible-Forge.git`

2. Run `./gradlew buildAllTheStuffNowBcGradleIsDumb` in the root directory of the project. (I love gradle so much)

    Note: You only need to run this once, after that you can just run `./gradlew build`. On future builds gradle will
    actually wait for the subprojects to build before merging the jars (usually). The workaround is needed due to how
    gradle's dependency graph works, causing it to look ahead and have a fit since the built Jars don't exist yet.

3. Run `./gradlew build` in the root directory of the project.

4. The jar will be in `build/libs/`
