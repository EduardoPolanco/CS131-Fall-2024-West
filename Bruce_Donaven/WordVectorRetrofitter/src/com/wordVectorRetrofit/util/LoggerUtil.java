package com.wordVectorRetrofit.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.wordVectorRetrofit.ui.TerminalConsole;
import javafx.application.Platform;

public class LoggerUtil {

    private static TerminalConsole terminalConsole;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void initialize(TerminalConsole console) {
        terminalConsole = console;
    }

    public static void log(String message) {
        log(message, LogLevel.INFO);
    }

    public static void log(String message, LogLevel level) {
        String timestamp = LocalDateTime.now().format(formatter);
        String formattedMessage = String.format("[%s] [%s] %s", timestamp, level.name(), message);

        if (terminalConsole != null) {
            Platform.runLater(() -> {
                switch (level) {
                    case FINE:
                        terminalConsole.appendText("[FINE] " + formattedMessage);
                        break;
                    case INFO:
                        terminalConsole.appendText("[INFO] " + formattedMessage);
                        break;
                    case WARNING:
                        terminalConsole.appendText("[WARNING] " + formattedMessage);
                        break;
                    case SEVERE:
                        terminalConsole.appendText("[SEVERE] " + formattedMessage);
                        break;
                }
            });
        } else {
            System.out.println(formattedMessage);
        }
    }

    public static void fine(String message) {
        log(message, LogLevel.FINE);
    }

    public static void info(String message) {
        log(message, LogLevel.INFO);
    }

    public static void warning(String message) {
        log(message, LogLevel.WARNING);
    }

    public static void severe(String message) {
        log(message, LogLevel.SEVERE);
    }

    public static void error(String message) {
        log(message, LogLevel.SEVERE);
    }

    private enum LogLevel {
        FINE,
        INFO,
        WARNING,
        SEVERE
    }
}
