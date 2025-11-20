# IGNORESERVERONLY String

## 1.13.2 - 1.16.5
```java
import net.minecraftforge.fml.network.FMLNetworkConstants;
import java.util.function.Supplier;

Supplier<String> IGNORE_SERVER_ONLY = () -> FMLNetworkConstants.IGNORESERVERONLY;
```

## 1.17.1
```java
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;
import java.util.function.Supplier;

Supplier<String> IGNORE_SERVER_ONLY = () -> FMLNetworkConstants.IGNORESERVERONLY;
```

## 1.18 - 1.19.4
```java
import net.minecraftforge.network.NetworkConstants;
import java.util.function.Supplier;

Supplier<String> IGNORE_SERVER_ONLY = () -> NetworkConstants.IGNORESERVERONLY;
```

## 1.20.1 - 1.21.10
```java
import net.minecraftforge.fml.IExtensionPoint;
import java.util.function.Supplier;

Supplier<String> IGNORE_SERVER_ONLY = () -> IExtensionPoint.DisplayTest.IGNORESERVERONLY;
```

# IGNORE SERVER VERSION DisplayTest

## 1.17.1

```java
import net.minecraftforge.fml.IExtensionPoint;
import java.util.function.Supplier;

Supplier<IExtensionPoint.DisplayTest> IGNORE_SERVER_VERSION =
        () -> new IExtensionPoint.DisplayTest(
                () -> FMLNetworkConstants.IGNORESERVERONLY,
                (remoteVersion, isFromServer) -> true);
```

## 1.18.2 - 1.21.10, excluding 1.19, 1.19.3, 1.20.2

```java
import net.minecraftforge.fml.IExtensionPoint;
import java.util.function.Supplier;

Supplier<IExtensionPoint.DisplayTest> IGNORE_SERVER_VERSION = IExtensionPoint.DisplayTest.IGNORE_SERVER_VERSION;
```

## 1.19, 1.19.3, 1.20.2

```java
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.network.NetworkConstants;
import java.util.function.Supplier;

Supplier<IExtensionPoint.DisplayTest> IGNORE_SERVER_VERSION =
        () -> new IExtensionPoint.DisplayTest(
                () -> NetworkConstants.IGNORESERVERONLY,
                (remoteVersion, isFromServer) -> true);
```

# ModLoadingContext#registerDisplayTest

## Forge 1.13.2 - 1.16.5

```java
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

ModLoadingContext.get().registerExtensionPoint(
        ExtensionPoint.DISPLAYTEST,
        () -> Pair.of(
                () -> FMLNetworkConstants.IGNORESERVERONLY,
                (remoteVersion, isFromServer) -> true));
```

## Forge 1.17.1

```java
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;

ModLoadingContext.get().registerExtensionPoint(
        IExtensionPoint.DisplayTest.class,
        () -> new IExtensionPoint.DisplayTest(
                () -> FMLNetworkConstants.IGNORESERVERONLY,
                (remoteVersion, isFromServer) -> true));
```

## Forge 1.18 - 1.19.4

```java
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

ModLoadingContext.get().registerExtensionPoint(
        IExtensionPoint.DisplayTest.class,
        () -> new IExtensionPoint.DisplayTest(
                () -> NetworkConstants.IGNORESERVERONLY,
                (remoteVersion, isFromServer) -> true));
```

## Forge 1.20.1 - 1.21.10

```java
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

ModLoadingContext.get().registerExtensionPoint(
        IExtensionPoint.DisplayTest.class,
        () -> new IExtensionPoint.DisplayTest(
                () -> IExtensionPoint.DisplayTest.IGNORESERVERONLY,
                (remoteVersion, isFromServer) -> true));
```

## Forge 1.18.2 - 1.21.10, excluding 1.19, 1.19.3, 1.20.2

```java
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

ModLoadingContext.get().registerExtensionPoint(
        IExtensionPoint.DisplayTest.class,
        IExtensionPoint.DisplayTest.IGNORE_SERVER_VERSION);

ModLoadingContext.get().registerDisplayTest(
        IExtensionPoint.DisplayTest.IGNORE_SERVER_VERSION);
```

# ModLoadingContext#registerConfig

## Forge 1.13.2 - 1.16.5

```java
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

ModLoadingContext.get()
    .registerConfig(ModConfig.Type.COMMON, (ForgeConfigSpec) spec, fileName);
```

## Forge 1.17.1 - 1.21.10

```java
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

ModLoadingContext.get()
    .registerConfig(ModConfig.Type.COMMON, (IConfigSpec) spec, fileName);
```

## Forge 1.19.2 - 1.21.10, excluding 1.19.3

```java
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

FMLJavaModLoadingContext.get()
    .registerConfig(ModConfig.Type.COMMON, Config.spec, fileName);

((FMLJavaModLoadingContext) ModLoadingContext.get().extension())
    .registerConfig(ModConfig.Type.COMMON, Config.spec, fileName);
```
