package org.adde0109.pcf.v1_17_1.forge;

import net.minecraftforge.fml.IExtensionPoint;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public final class PCFBootstrap {
    public static Supplier<@NotNull String> IGNORE_SERVER_ONLY =
            () -> {
                throw new IllegalStateException("Not initialized");
            };

    public static final BiPredicate<@NotNull String, Boolean> REMOTE_VERSION_TEST =
            (remoteVersion, isFromServer) -> true;

    public static Supplier<IExtensionPoint.@NotNull DisplayTest> IGNORE_SERVER_VERSION =
            () -> new IExtensionPoint.DisplayTest(IGNORE_SERVER_ONLY, REMOTE_VERSION_TEST);
}
