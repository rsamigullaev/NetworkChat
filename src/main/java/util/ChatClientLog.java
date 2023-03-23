package util;

import model.ChatResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public final class ChatClientLog {
    private final File logFile;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public ChatClientLog() {
        final var currentDir = new File(System.getProperty("user.dir").replaceAll("\\\\", "/"));
        logFile = new File(currentDir, "file.log");
        try {
            if (!logFile.exists()) logFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Не удалось создать файл для записи логов.");
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void writeMessage(final String message) {
        try {
            if (!logFile.exists()) logFile.createNewFile();
            Files.writeString(logFile.toPath(), message, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeMessage(final ChatResponse response) {
        final var message = "[%1$td.%1$tm.%1$tY %1$tH:%1$tM:%1$tS]\t%2$s: %3$s\r\n".formatted(
                response.senderTimestamp(),
                response.senderUsername(),
                response.senderContent()
        );
        writeMessage(message);
    }
}
