package org.adde0109.pcf.v17_1.forge;

import net.minecraftforge.fml.IExtensionPoint;

import org.jspecify.annotations.NonNull;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public final class PCFBootstrap {
    public static Supplier<@NonNull String> IGNORE_SERVER_ONLY =
            () -> {
                throw new IllegalStateException("Not initialized");
            };

    public static final BiPredicate<@NonNull String, Boolean> REMOTE_VERSION_TEST =
            (remoteVersion, isFromServer) -> true;

    public static Supplier<IExtensionPoint.@NonNull DisplayTest> IGNORE_SERVER_VERSION =
            () -> new IExtensionPoint.DisplayTest(IGNORE_SERVER_ONLY, REMOTE_VERSION_TEST);
}
