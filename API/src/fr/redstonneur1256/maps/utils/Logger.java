package fr.redstonneur1256.maps.utils;

import org.jetbrains.annotations.Nullable;

public class Logger {

    private static LoggerImpl impl;

    public static void setImpl(LoggerImpl impl) {
        Logger.impl = impl;
    }

    public static void log(String message) {
        log(message, null);
    }

    public static void log(String message, Throwable throwable) {
        if(impl != null) {
            impl.log(message, throwable);
        }
    }

    @FunctionalInterface
    public interface LoggerImpl {

        void log(@Nullable String message, @Nullable Throwable throwable);

    }

}
