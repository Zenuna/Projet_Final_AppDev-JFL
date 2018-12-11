package cgg.informatique.jfl.webSocket.configurations;

import cgg.informatique.jfl.webSocket.Message;
import cgg.informatique.jfl.webSocket.Reponse;
import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;

public class MonStompSessionHandler implements StompSessionHandler {


    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {

    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {

    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return null;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {

    }
}
