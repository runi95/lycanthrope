package lycanthrope.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import lycanthrope.models.*;
import lycanthrope.services.LobbyService;
import lycanthrope.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class WebSocketController {

    @Autowired
    LobbyService lobbyService;

    @Autowired
    UserService userService;

    private Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @MessageMapping("/joinLobby")
    @SendTo(value = "/endpoint/broadcast")
    public WebSocketResponseMessage<String> joinLobby(WebSocketRequestMessage webSocketRequestMessage, Principal principal) {
        String boardIdString = webSocketRequestMessage.getValue();
        Integer boardId = null;
        try {
            boardId = Integer.parseInt(boardIdString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (boardId == null) {
            return errorResponse("boardId can't be null");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            return errorResponse("could not find the user this message came from");
        }

        Optional<Lobby> optionalLobby = lobbyService.joinLobby(boardId, optionalUser.get());
        if (!optionalLobby.isPresent()) {
            return errorResponse("failed to join the lobby");
        }

        WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
        webSocketResponseMessage.setAction("loadlobby");
        webSocketResponseMessage.setContent(optionalLobby.get());
        webSocketResponseMessage.setStatus(200);

        return webSocketResponseMessage;
    }

    @MessageMapping("/getLobbies")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<List<Lobby>> getLobbies() {
        List<Lobby> lobbies = lobbyService.getAllLobbies();
        WebSocketResponseMessage<List<Lobby>> message = new WebSocketResponseMessage<>();

        message.setContent(lobbies);
        message.setAction("populatelobbies");
        message.setStatus(200);

        return message;
    }

    private WebSocketResponseMessage errorResponse(String message) {
        logger.warn(message);

        WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
        webSocketResponseMessage.setContent(message);
        webSocketResponseMessage.setStatus(500);

        return webSocketResponseMessage;
    }
}
