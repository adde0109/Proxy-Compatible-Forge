# General Cross-Version Notes

## 1.15.2 -> 1.16.1

- ModernForwardingMixin
  - `net.minecraft.network.chat.TextComponent#<init>(String)`
    changes to `net.minecraft.network.chat.Component#nullToEmpty()`

- TODO: Implement brigadier argument wrapping

- WrappableCommandsPacketMixin
  - `net.minecraft.network.protocol.game.ClientboundCommandsPacket
    #getNodesInIdOrder(Object2IntMap<CommandNode<SharedSuggestionProvider>>)` added
  - `ClientboundCommandsPacket#writeNode` becomes static
  - `ClientboundCommandsPacket#enumerateNodes(RootCommandNode<SharedSuggestionProvider>)` added

## 1.16.1 -> 1.16.5

- CommandsMixin
  - `fml:conndata` doesn't exist until 1.16.5
ModernForwarding
  - `FriendlyByteBuf#readUtf()` is client-only, 
  requiring the usage of `FriendlyByteBuf#readUtf(Short.MAX_VALUE)`

## 1.16.5 -> 1.17.1

- Initializer
  - `String#isBlank()` is not available until 1.17.1 due to Java 11
  - ```java
    ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
            () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    ```
    changes to
    ```java
    ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
            () -> new IExtensionPoint.DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    ```
    - `net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent`
      changes to `net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent`

- ArgumentTypesEntryUtil/StateUtil reflection changes
  - Forge intermediary mappings reset in 1.17.1

- CommandsMixin
  - `net.minecraftforge.fml.network.FMLConnectionData`
    changed to `net.minecraftforge.fmllegacy.network.FMLConnectionData`

- ModernForwardingMixin
  - `net.minecraft.network.protocol.login.ClientboundCustomQueryPacket#<init>()`
    changes to `ClientboundCustomQueryPacket#<init>(int, ResourceLocation, ByteBuf)`

- removed ClientboundCustomQueryPacketAccessor

- WrappableCommandsPacketMixin
  - `FriendlyByteBuf#writeCollection` doesn't exist until 1.17.1
  - `ClientboundCommandsPacket#getNodesInIdOrder` returns `List<CommandNode<SharedSuggestionProvider>>`
    instead of `CommandNode<SharedSuggestionProvider>[]`

## 1.17.1 -> 1.18

- Initializer
  - ```java
    ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
            () -> new IExtensionPoint.DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    ```
    changes to
    ```java
    ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
        () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    ```
  - `net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent`
    changes to `net.minecraftforge.event.server.ServerAboutToStartEvent`

- CommandsMixin
 - `net.minecraftforge.fmllegacy.network.FMLConnectionData`
   changed to `net.minecraftforge.network.ConnectionData`

## 1.18 -> 1.19

- Initializer
  - ```java
    ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
        () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    ```
    changes to
    ```java
    ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
            () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));
    ```
- added WrappableArgumentNodeStubMixin
- removed IMixinWrappableCommandPacket
- removed CommandsMixin
- removed WrappableCommandsPacketMixin

## 1.19 -> 1.19.1

- WrappableArgumentNodeStubMixin
  - `net.minecraftforge.registries.ForgeRegistries#COMMAND_ARGUMENT_TYPES`
    does not exist until 1.19.1

## 1.19.1 -> 1.19.3

- WrappableArgumentNodeStubMixin
  - `net.minecraft.core.Registry#COMMAND_ARGUMENT_TYPE`
    changed to `net.minecraft.core.registries.BuiltInRegistries#COMMAND_ARGUMENT_TYPE`

## 1.19.3 -> 1.20.2

- ModernForwarding
  - `net.minecraft.network.protocol.login.ServerboundCustomQueryPacket`
    renamed to `net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket`
  - ```java
    FriendlyByteBuf data = packet.getInternalData();
    if(data == null) {
        throw new Exception("Got empty packet");
    }
    ```
    changes to
    ```java
    FriendlyByteBuf data = packet.getInternalData();
    if(packet.payload() == null) {
        throw new Exception("Got empty packet");
    }
    FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
    packet.payload().write(data);
    ```
- CustomQueryAnswerPayloadImpl added
- ServerboundCustomQueryAnswerPacketMixin added

- ModernForwardingMixin
  - `net.minecraft.network.protocol.login.ServerboundCustomQueryPacket`
    renamed to `net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket`
  - ```java
    import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
    new ClientboundCustomQueryPacket(index, resourceLocation, byteBuff);
    ```
    replaced with
    ```java
    import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
    import net.minecraft.network.protocol.login.custom.DiscardedQueryPayload;
    new ClientboundCustomQueryPacket(index, new DiscardedQueryPayload(resourceLocation, byteBuff));
    ```
  - `@Shadow @Nullable public GameProfile gameProfile;`
  changed to
  `@Shadow @Nullable private GameProfile authenticatedProfile;`
  - `ServerLoginPacketListenerImpl.State.NEGOTIATING` ordinal changes from 3 to 4

## 1.20.2 -> 1.20.6

- StateUtil
  - Reflection changes due to runtime Mojmaps

- ModernForwardingMixin
  - ```java
    import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
    import net.minecraft.network.protocol.login.custom.DiscardedQueryPayload;
    new ClientboundCustomQueryPacket(index, new DiscardedQueryPayload(resourceLocation, byteBuff));
    ```
    replaced with
    ```java
    import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
    import net.minecraft.network.protocol.login.custom.DiscardedQueryPayload;
    new ClientboundCustomQueryPacket(index, new DiscardedQueryPayload(resourceLocation));
    ```
  - `ServerboundCustomQueryAnswerPacket#getIndex()` changes to `ServerboundCustomQueryAnswerPacket#transactionId()`
