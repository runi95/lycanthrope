package spaceworms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import spaceworms.models.*;
import spaceworms.services.APIService;
import spaceworms.services.LobbyService;
import spaceworms.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class WebSocketController {

    @Autowired
    APIService api;

    @Autowired
    LobbyService lobbyService;

    @Autowired
    UserService userService;

    @MessageMapping("/getBoards")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<List<Board>> getBoards() {
        Optional<List<Board>> optionalBoards = api.getBoards(null);
        WebSocketResponseMessage<List<Board>> message = new WebSocketResponseMessage<>();

        if (optionalBoards.isPresent()) {
            message.setContent(optionalBoards.get());
            message.setAction("populateboardtable");
            message.setStatus(200);
        } else {
            message.setContent(null);
            message.setStatus(500);
        }

        return message;
    }

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
            WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
            webSocketResponseMessage.setContent(null);
            webSocketResponseMessage.setStatus(500);

            return webSocketResponseMessage;
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
            webSocketResponseMessage.setContent(null);
            webSocketResponseMessage.setStatus(500);

            return webSocketResponseMessage;
        }

        Optional<Lobby> optionalLobby = lobbyService.joinLobby(boardId, optionalUser.get());
        if (!optionalLobby.isPresent()) {
            WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
            webSocketResponseMessage.setContent(null);
            webSocketResponseMessage.setStatus(500);

            return webSocketResponseMessage;
        }

        WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
        webSocketResponseMessage.setAction("loadlobby");
        webSocketResponseMessage.setContent(optionalLobby.get());
        webSocketResponseMessage.setStatus(200);

        return webSocketResponseMessage;
    }

    @MessageMapping("/getLobby")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<List<User>> getLobby() {
        return null;
    }
}
