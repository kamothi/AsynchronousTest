package com.kim.websocketTest.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExampleHandler implements WebSocketHandler {
    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        sessions.add(webSocketSession);

        Flux<WebSocketMessage> stringFlux = webSocketSession.receive()
                .doOnNext(message -> {
                    log.info("Received message: {}", message.getPayloadAsText());
                    broadcastLocation(webSocketSession, message.getPayloadAsText());
                })
                .map(webSocketMessage -> {
                    String senderId = webSocketSession.getId();
                    return senderId + ": Acknowledged";
                })
                .map(webSocketSession::textMessage);

        return webSocketSession.send(stringFlux);
    }

    private void broadcastLocation(WebSocketSession sender, String location) {
        String senderId = sender.getId();
        sessions.stream()
                .filter(session -> !session.getId().equals(senderId))
                .forEach(session -> {
                    session.send(Mono.just(session.textMessage("User " + senderId + " sent location: " + location)))
                            .subscribe();
                });
    }
}
