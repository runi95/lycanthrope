package spaceworms.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(WebSocketController.class);

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
            return errorResponse("failed to retrieve boards from the api");
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

    @MessageMapping("/rollDice")
    @SendTo(value = "/endpoint/broadcast")
    public WebSocketResponseMessage<String> rollDice(Principal principal) throws Exception {
        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            throw new Exception("No player with the given nickname could be found!");
        }

        if (!optionalUser.get().getLobby().isStarted()) {
            throw new Exception("You can't roll the die before the game starts!");
        }

        if (optionalUser.get().getPlayerNumber() != optionalUser.get().getLobby().getCurrentPlayerId()) {
            throw new Exception("You can't roll the die on someone else's turn!");
        }

        DiceThrowResult diceThrowResult = lobbyService.rollDice(optionalUser.get());

        WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
        webSocketResponseMessage.setAction("diceresult");
        webSocketResponseMessage.setContent(diceThrowResult);
        webSocketResponseMessage.setStatus(200);

        return webSocketResponseMessage;
    }

    private WebSocketResponseMessage errorResponse(String message) {
        logger.warn(message);

        WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
        webSocketResponseMessage.setContent(message);
        webSocketResponseMessage.setStatus(500);

        return webSocketResponseMessage;
    }
}
