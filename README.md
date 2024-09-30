# Modern Forwarding for Forge
This mod brings modern forwarding to Forge servers (See: https://docs.papermc.io/velocity/player-information-forwarding)

## How to get started:
1. Download and install this as a mod to your Forge server. (Jars can be found in the releases tab.)
2. Start the server.
3. Close the server and open "pcf-common.toml" in the config folder and put your forwarding secret in the "forwardingSecret" field.
4. In "server.properties" make sure online-mode is set to false.
5. You are now ready to start the server and connect to it with Velocity!

Note: Latest version (> 1.1.7) of Ambassador doesn't require this mod anymore but you can still use it if you want modern forwarding.

## Building the Project

1. Clone the repository. `git clone https://github.com/adde0109/Proxy-Compatible-Forge.git`

2. Run `./gradlew buildAllTheStuffNowBcGradleIsDumb` in the root directory of the project. (I love gradle so much)

    Note: You only need to run this once, after that you can just run `./gradlew build`. On future builds gradle will
    actually wait for the subprojects to build before merging the jars (usually). The workaround is needed due to how
    gradle's dependency graph works, causing it to look ahead and have a fit since the built Jars don't exist yet.

3. Run `./gradlew build` in the root directory of the project.

4. The jar will be in `build/libs/`
