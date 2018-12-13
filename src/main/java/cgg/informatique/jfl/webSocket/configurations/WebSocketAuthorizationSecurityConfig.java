package cgg.informatique.jfl.webSocket.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;


@Configuration
public class WebSocketAuthorizationSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {


    @Override
    protected void configureInbound(final MessageSecurityMetadataSourceRegistry messages) {

    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}

