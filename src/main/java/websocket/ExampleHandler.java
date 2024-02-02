package websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class ExampleHandler implements WebSocketHandler {

    private final CopyOnWriteArraySet<WebSocketSession> clients = new CopyOnWriteArraySet<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<String> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(message -> broadcastMessage(session.getId(), message));

        return session.send(input.map(session::textMessage));
    }

    private void broadcastMessage(String senderId, String message) {
        clients.forEach(client -> {
            if (!client.getId().equals(senderId)) {
                // Assume message format: "x,y"
                String[] parts = message.split(",");
                if (parts.length == 2) {
                    // Broadcast x, y values to other players
                    String formattedMessage = senderId + ": x=" + parts[0] + ", y=" + parts[1];
                    WebSocketMessage textMessage = client.textMessage(formattedMessage);
                    client.send(Mono.just(textMessage)).subscribe();
                }
            }
        });
    }
}
