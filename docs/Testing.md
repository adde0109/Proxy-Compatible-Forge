# Testing

Just a quick outline on for regression testing in PCF, primarily so I don't forget the list.

Notes:
- Forge's `forge:enum` and `forge:modid` should be a good baseline whether CrossStitch wrapping is working.
    - We do need to find/make a cross-version mod that implements custom args to fully test things for sure, however.
- Vanilla/Neo/Forge clients should all be able to connect to (mod-less) Forge/NeoForge servers using modern forwarding.

## "Quick" Test Matrix

Used before a release to check for obvious regressions.

| MC Version | Server          | Client          |
|------------|-----------------|-----------------|
| 1.14.4     | Forge           | Forge           |
| 1.16.5     | Forge           | Forge           |
| 1.17.1     | Forge           | Forge           |
| 1.20.4     | Forge, NeoForge | Forge, NeoForge |
| 1.21.1     | Forge, NeoForge | Forge, NeoForge |
| 1.21.5     | Forge, NeoForge | Forge, NeoForge |
| 1.21.11    | Forge, NeoForge | Forge, NeoForge |

## Full Test Matrix

Used when swathing changes are made to ensure no regressions across supported versions.

| MC Version | Server          | Client          |
|------------|-----------------|-----------------|
| 1.14.4     | Forge           | Forge           |
| 1.15.2     | Forge           | Forge           |
| 1.16.5     | Forge           | Forge           |
| 1.17.1     | Forge           | Forge           |
| 1.18.2     | Forge           | Forge           |
| 1.19       | Forge           | Forge           |
| 1.19.1     | Forge           | Forge           |
| 1.19.2     | Forge           | Forge           |
| 1.19.4     | Forge           | Forge           |
| 1.20.1     | Forge           | Forge           |
| 1.20.2     | Forge, NeoForge | Forge, NeoForge |
| 1.20.4     | Forge, NeoForge | Forge, NeoForge |
| 1.21.1     | Forge, NeoForge | Forge, NeoForge |
| 1.21.5     | Forge, NeoForge | Forge, NeoForge |
| 1.21.11    | Forge, NeoForge | Forge, NeoForge |
