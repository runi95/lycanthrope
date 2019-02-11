package lycanthrope.controllers;

import freemarker.template.Template;
import lycanthrope.services.PlayerRoleService;
import lycanthrope.services.PlayerService;
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
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.StringWriter;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class WebSocketController {

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerRoleService playerRoleService;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

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
                    //case "beholder":
                    //    lobby.addRole(Roles.BEHOLDER);
                    //    break;
                    case "drunk":
                        lobby.addRole(Roles.DRUNK);
                        break;
                    case "insomniac":
                        lobby.addRole(Roles.INSOMNIAC);
                        break;
                    //case "squire":
                    //    lobby.addRole(Roles.SQUIRE);
                    //    break;
                    //case "auraseer":
                    //    lobby.addRole(Roles.AURASEER);
                    //    break;
                    case "troublemaker":
                        lobby.addRole(Roles.TROUBLEMAKER);
                        break;
                    case "robber":
                        lobby.addRole(Roles.ROBBER);
                        break;
                    case "seer":
                        lobby.addRole(Roles.SEER);
                        break;
                    //case "thing":
                    //    lobby.addRole(Roles.THING);
                    //    break;
                    case "mason1":
                        lobby.addRole(Roles.MASON1);
                        break;
                    case "mason2":
                        lobby.addRole(Roles.MASON2);
                        break;
                    //case "apprenticetanner":
                    //    lobby.addRole(Roles.APPRENTICETANNER);
                    //    break;
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

    @MessageMapping("/nightAction")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<String> nightAction(WebSocketRequestMessage<String> webSocketRequestMessage, Principal principal) throws Exception {
        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            throw new Exception("Could not find a user with the given nickname");
        }

        if (webSocketRequestMessage.getValue() == null || webSocketRequestMessage.getValue().length() < 1) {
            throw new Exception("Could not understand performed action");
        }

        return performNightAction(optionalUser.get(), webSocketRequestMessage.getValue(), optionalUser.get().getPlayer().getRoleId());
    }

    private WebSocketResponseMessage errorResponse(String message) {
        logger.warn(message);

        WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
        webSocketResponseMessage.setContent(message);
        webSocketResponseMessage.setStatus(500);

        return webSocketResponseMessage;
    }

    private WebSocketResponseMessage<String> parseTemplate(String templateName, Map model) throws Exception {
        StringWriter stringWriter = new StringWriter();

        Template temp = freeMarkerConfig.getConfiguration().getTemplate(templateName + ".ftlh");
        temp.process(model, stringWriter);

        WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
        webSocketResponseMessage.setAction("changeView");
        webSocketResponseMessage.setContent(stringWriter.getBuffer().toString());
        webSocketResponseMessage.setStatus(200);

        return webSocketResponseMessage;
    }

    // TODO: Move the below functions to somewhere else

    private WebSocketResponseMessage<String> performNightAction(User user, String messageValue, int roleID) throws Exception {
        // Target is another player
        if (messageValue.charAt(0) == 'u') {
            int targetUserId = Integer.parseInt(messageValue.substring(1));

            Optional<User> optionalTargetUser = userService.findById(targetUserId);
            if (!optionalTargetUser.isPresent()) {
                throw new Exception("Could not find a user with the given target id");
            }

            if (user.getLobby().getId() != optionalTargetUser.get().getLobby().getId()) {
                throw new Exception("Someone tried to target a user that isn't in their lobby");
            }

            switch (roleID) {
                case 3: // Troublemaker
                    break;
                case 4: // Robber
                    return performRobberAction(user, optionalTargetUser.get(), user.getPlayer().getActionsPerformed(), messageValue);
                case 5: // Seer
                    break;
                case 10: // Doppelganger
                    return performDoppelgangerAction(user, optionalTargetUser.get(), user.getPlayer().getActionsPerformed(), messageValue);
                default: // Something went wrong
                    throw new Exception("Someone tried to perform an action with a role that can't perform this action");
            }

            // Target is a neutral card
        } else if (messageValue.charAt(0) == 'n') {
            int targetNeutralId = Integer.parseInt(messageValue.substring(1));

            switch (roleID) {
                case 1: // Drunk
                    return performDrunkAction(user, user.getPlayer().getActionsPerformed(), messageValue);
                case 5: // Seer
                    break;
                case 14: // Werewolf 1
                    return performWerewolfAction(user, user.getPlayer().getActionsPerformed(), messageValue);
                case 15: // Werewolf 2
                    return performWerewolfAction(user, user.getPlayer().getActionsPerformed(), messageValue);
                default: // Something went wrong
                    throw new Exception("Someone tried to perform an action with a role that can't perform this action");
            }
        } else {
            throw new Exception("Could not understand what target you chose");
        }

        throw new Exception("Something went wrong");
    }

    private WebSocketResponseMessage<String> performRobberAction(User user, User target, int actionsPerformed, String messageValue) throws Exception {
        if (actionsPerformed > 0) {
            throw new Exception("You have already performed all your actions");
        }

        user.getPlayer().setActionsPerformed(user.getPlayer().getActionsPerformed() + 1);
        user.getPlayer().setNightActionTarget(messageValue);
        playerService.save(user.getPlayer());

        Map map = new HashMap();
        map.put("viewRole", playerRoleService.getRole(target.getPlayer().getRoleId()).getName());

        return parseTemplate("gameNightAction", map);
    }

    private WebSocketResponseMessage<String> performDoppelgangerAction(User user, User target, int actionsPerformed, String messageValue) throws Exception {
        if (actionsPerformed == 1) {
            return performNightAction(user, messageValue, target.getPlayer().getRoleId());
        } else if (actionsPerformed == 0) {
            user.getPlayer().setActionsPerformed(user.getPlayer().getActionsPerformed() + 1);
            user.getPlayer().setNightActionTarget(messageValue);
            playerService.save(user.getPlayer());

            NightAction[] nightActions = playerRoleService.getRole(user.getPlayer().getRoleId()).getNightActions(user.getLobby());

            Map map = new HashMap();
            map.put("viewRole", playerRoleService.getRole(target.getPlayer().getRoleId()).getName());
            map.put("userid", user.getId());

            if (nightActions.length > 0) {
                map.put("nightAction", nightActions[0]);
            }

            return parseTemplate("gameNightAction", map);
        } else {
            throw new Exception("You have already performed all your actions");
        }
    }

    private WebSocketResponseMessage<String> performDrunkAction(User user, int actionsPerformed, String messageValue) throws Exception {
        if (actionsPerformed > 0) {
            throw new Exception("You have already performed all your actions");
        }

        user.getPlayer().setActionsPerformed(user.getPlayer().getActionsPerformed() + 1);
        user.getPlayer().setNightActionTarget(messageValue);
        playerService.save(user.getPlayer());

        return parseTemplate("gameNightAction", null);
    }

    private WebSocketResponseMessage<String> performWerewolfAction(User user, int actionsPerformed, String messageValue) throws Exception {
        if (actionsPerformed > 0) {
            throw new Exception("You have already performed all your actions");
        }

        user.getPlayer().setActionsPerformed(user.getPlayer().getActionsPerformed() + 1);
        user.getPlayer().setNightActionTarget(messageValue);
        playerService.save(user.getPlayer());

        return parseTemplate("gameNightAction", null);
    }
}
