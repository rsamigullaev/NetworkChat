package network;

import model.ChatMessage;
import model.ChatResponse;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import util.ChatClientSettings;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public final class ChatClientNetwork {
    public static final String DESTINATION_MESSAGE = "/app/message";
    public static final String DESTINATION_ENTER = "/app/enter";
    public static final String DESTINATION_EXIT = "/app/exit";

    private final ChatClientSettings settings;
    private final StompSession stompSession;

    public ChatClientNetwork(final ChatClientSettings settings, final Consumer<ChatResponse> frameListener) {
        this.settings = settings;

        final var port = settings.get("port");
        if (port == null) throw new IllegalArgumentException("Не указан порт");

        final var client = new StandardWebSocketClient();

        final var stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        final var sessionHandler = new ChatClientStompSessionHandler(frameListener);
        try {
            this.stompSession = stompClient.connect("ws://localhost:%s/cs".formatted(port), sessionHandler).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        this.stompSession.disconnect();
    }

    public StompSession.Receiptable sendMessage(
            final String userId,
            final String nickname,
            final String content
    ) {
        final var body = new ChatMessage(userId, nickname, content, new Date(System.currentTimeMillis()));
        if (settings.isDebug()) System.out.printf("Body: %s\r\n", body);
        return this.stompSession.send(DESTINATION_MESSAGE, body);
    }

    public StompSession.Receiptable sendEnter(final String userId, final String username) {
        final var body = new ChatMessage(userId, username, null, new Date(System.currentTimeMillis()));
        if (settings.isDebug()) System.out.printf("Body: %s\r\n", body);
        return this.stompSession.send(DESTINATION_ENTER, body);
    }

    public StompSession.Receiptable sendExit(final String userId, final String username) {
        final var body = new ChatMessage(userId, username, null, new Date(System.currentTimeMillis()));
        if (settings.isDebug()) System.out.printf("Body: %s\r\n", body);
        return this.stompSession.send(DESTINATION_EXIT, body);
    }
}
