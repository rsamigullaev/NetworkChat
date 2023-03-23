package network;

import model.ChatResponse;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public class ChatClientStompSessionHandler implements StompSessionHandler {
    private final Consumer<ChatResponse> frameListener;

    public ChatClientStompSessionHandler(final Consumer<ChatResponse> frameListener) {
        this.frameListener = frameListener;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/chat", this);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        exception.printStackTrace();
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        exception.printStackTrace();
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ChatResponse.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        frameListener.accept((ChatResponse) payload);
    }
}
