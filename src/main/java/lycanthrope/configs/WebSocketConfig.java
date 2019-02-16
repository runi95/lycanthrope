package lycanthrope.configs;

import lycanthrope.models.Lobby;
import lycanthrope.models.User;
import lycanthrope.models.WebSocketResponseMessage;
import lycanthrope.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        messageBrokerRegistry.enableSimpleBroker("/endpoint");
        messageBrokerRegistry.setApplicationDestinationPrefixes("/websock");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint("/lycanthrope").withSockJS();
    }

    @EventListener
    public void onSocketDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        Optional<User> optionalUser = userService.findByNickname(sha.getUser().getName());

        // This can techincally become null somewhere between saving the user to DB and when the user connects to our web socket endpoint.
        // TODO: Make sure we handle this exception properly!
        if (!optionalUser.isPresent()) {
            logger.error("Could not find the leaving player in our DB!");

            return;
        }

        logger.info(sha.getUser().getName() + " has disconnected from the web socket");

        if (optionalUser.get().getLobby() == null) {
            return;
        }

        if (optionalUser.get().getLobby().getState() > 1 || optionalUser.get().getLobby().getUsers().size() > 2) {
            Lobby lobby = optionalUser.get().getLobby();
            lobby.removeUser(optionalUser.get());
            userService.save(optionalUser.get());

            WebSocketResponseMessage<User> webSocketResponseMessage = new WebSocketResponseMessage<>();
            webSocketResponseMessage.setStatus(200);
            webSocketResponseMessage.setAction("disconnected");
            webSocketResponseMessage.setContent(optionalUser.get());
            simpMessagingTemplate.convertAndSend("/endpoint/broadcast", webSocketResponseMessage);
        }
    }
}