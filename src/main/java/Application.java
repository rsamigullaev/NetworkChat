import model.ChatResponse;
import network.ChatClientNetwork;
import ui.ChatClientUI;
import ui.Command;
import util.ChatClientLog;
import util.ChatClientSettings;

import java.util.Objects;
import java.util.UUID;

public final class Application {
    private final ChatClientUI ui = new ChatClientUI();
    private final ChatClientLog log = new ChatClientLog();
    private final ChatClientSettings settings = new ChatClientSettings("/settings.txt");
    private final ChatClientNetwork network = new ChatClientNetwork(settings, this::frameListener);

    private final String userId = UUID.randomUUID().toString();
    private String username;

    public void launch() {
        while (username == null || username.isBlank()) {
            if ((username = settings.get("username")) == null) {
                System.out.print("Введите имя пользователя: ");
                username = ui.scanner.nextLine();
            }
        }
        network.sendEnter(userId, username);

        var input = ui.scanner.nextLine();
        while (!Objects.equals(input, Command.Exit.getValue())) {
            if (input.isBlank()) continue;

            network.sendMessage(userId, username, input);

            input = ui.scanner.nextLine();
        }

        network.sendExit(userId, username);
        System.out.printf("Пока, %s!", username);
        network.disconnect();
    }

    public void frameListener(final ChatResponse response) {
        switch (response.event()) {
            case Message -> {
                log.writeMessage(response);
                System.out.printf(
                        "[%1$td.%1$tm.%1$tY %1$tH:%1$tM:%1$tS]\t%2$s: %3$s\r\n",
                        response.senderTimestamp(),
                        response.senderUsername(),
                        response.senderContent()
                );
            }
            case Enter -> {
                if (isCurrentUser(response)) {
                    System.out.printf("Добро пожаловать, %s!\r\n", response.senderUsername());
                    System.out.print("Можете начать общение с другими\r\n");
                } else {
                    System.out.printf(
                            "[%1$td.%1$tm.%1$tY %1$tH:%1$tM:%1$tS]\t%2$s вступил(а) в чат!\r\n",
                            response.senderTimestamp(),
                            response.senderUsername()
                    );
                }
            }
            case Exit -> {
                if (!isCurrentUser(response)) {
                    System.out.printf(
                            "[%1$td.%1$tm.%1$tY %1$tH:%1$tM:%1$tS]\t%2$s покинул(а) чат!\r\n",
                            response.senderTimestamp(),
                            response.senderUsername()
                    );
                }
            }
        }
    }

    private boolean isCurrentUser(final ChatResponse response) {
        return Objects.equals(userId, response.senderId());
    }

    public static void main(final String[] args) {
        new Application().launch();
    }
}
