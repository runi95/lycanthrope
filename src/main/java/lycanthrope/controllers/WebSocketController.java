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
import java.util.Map;
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
    public WebSocketResponseMessage<String> joinLobby(WebSocketRequestMessage<String> webSocketRequestMessage, Principal principal) {
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

    @MessageMapping("/createLobby")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<Pair<Lobby, String>> createLobby(WebSocketRequestMessage<Pair<Map<String, String>, Integer>> webSocketRequestMessage, Principal principal) throws Exception {
        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            throw new Exception("Could not find a user with the given nickname");
        }

        Lobby lobby = new Lobby();

        for (String roleName : webSocketRequestMessage.getValue().getKey().keySet()) {
            if (webSocketRequestMessage.getValue().getKey().get(roleName).equals("true")) {
                switch (roleName) {
                    case "hunter":
                        lobby.addRole(Roles.HUNTER);
                        break;
                    case "beholder":
                        lobby.addRole(Roles.BEHOLDER);
                        break;
                    case "drunk":
                        lobby.addRole(Roles.DRUNK);
                        break;
                    case "insomniac":
                        lobby.addRole(Roles.INSOMNIAC);
                        break;
                    case "squire":
                        lobby.addRole(Roles.SQUIRE);
                        break;
                    case "auraseer":
                        lobby.addRole(Roles.AURASEER);
                        break;
                    case "troublemaker":
                        lobby.addRole(Roles.TROUBLEMAKER);
                        break;
                    case "robber":
                        lobby.addRole(Roles.ROBBER);
                        break;
                    case "seer":
                        lobby.addRole(Roles.SEER);
                        break;
                    case "thing":
                        lobby.addRole(Roles.THING);
                        break;
                    case "mason1":
                        lobby.addRole(Roles.MASON1);
                        break;
                    case "mason2":
                        lobby.addRole(Roles.MASON2);
                        break;
                    case "apprenticetanner":
                        lobby.addRole(Roles.APPRENTICETANNER);
                        break;
                    case "tanner":
                        lobby.addRole(Roles.TANNER);
                        break;
                    case "minion":
                        lobby.addRole(Roles.MINION);
                        break;
                    case "doppelganger":
                        lobby.addRole(Roles.DOPPELGANGER);
                        break;
                    case "villager1":
                        lobby.addRole(Roles.VILLAGER1);
                        break;
                    case "villager2":
                        lobby.addRole(Roles.VILLAGER2);
                        break;
                    case "villager3":
                        lobby.addRole(Roles.VILLAGER3);
                        break;
                    case "werewolf1":
                        lobby.addRole(Roles.WEREWOLF1);
                        break;
                    case "werewolf2":
                        lobby.addRole(Roles.WEREWOLF2);
                        break;
                    default:
                        break;
                }
            }
        }


        lobby.setName(optionalUser.get().getNickname() + "'s Lobby");
        lobby.setLobbyMaxSize(webSocketRequestMessage.getValue().getVal());

        lobbyService.save(lobby);

        lobby.addUser(optionalUser.get());

        lobbyService.save(lobby);

        WebSocketResponseMessage<Pair<Lobby, String>> message = new WebSocketResponseMessage<>();

        Pair<Lobby, String> pair = new Pair<>();
        pair.setKey(lobby);
        pair.setVal(optionalUser.get().getNickname());

        message.setContent(pair);
        message.setAction("createLobby");
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
