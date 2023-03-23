package util;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class ChatClientSettings {
    private final Map<String, String> map = new HashMap<>();

    public ChatClientSettings(final String resourceName) {
        try {
            final var currentDir = System.getProperty("user.dir").replaceAll("\\\\", "/");
            final var path = Paths.get(currentDir + resourceName);
            final var pairs = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (final String pair : pairs) {
                try {
                    final var p = pair.split("=");
                    if (!p[1].isBlank()) map.put(p[0], p[1].trim());
                } catch (final Throwable cause) {
                    cause.printStackTrace();
                }
            }

            System.out.printf("Доступные настройки: %s\r\n", map);
        } catch (final Throwable cause) {
            System.out.println("Не удалось загрузить настройки");
            cause.printStackTrace();
        }
    }

    public String get(final String key) {
        return map.get(key);
    }

    public boolean isDebug() {
        final var isDebug = map.get("isDebug");
        if (isDebug == null) return false;
        try {
            return isDebug.equals("true");
        } catch (final ClassCastException ignored) {
            return false;
        }
    }
}
