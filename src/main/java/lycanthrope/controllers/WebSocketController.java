package lycanthrope.controllers;

import lycanthrope.models.*;
import lycanthrope.models.roles.Hunter;
import lycanthrope.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
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
    private FreemarkerService freemarkerService;

    @Autowired
    private GameResultService gameResultService;

    private Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // TODO: Make sure you can only view game results about games you've played in (very low priority)
    @MessageMapping("/requestGameResult/{gameResultId}")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<String> requestGameResult(@DestinationVariable String gameResultId, Principal principal) throws Exception {
        if (gameResultId == null) {
            throw new Exception("Invalid gameResultId");
        }

        if (gameResultId.length() < 1) {
            throw new Exception("gameResultId has invalid length");
        }

        Long gameResultLong = null;
        try {
            gameResultLong = Long.parseLong(gameResultId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (gameResultLong == null || gameResultLong < 1) {
            throw new Exception("Bad gameResultLong");
        }

        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        Optional<GameResult> optionalGameResult = gameResultService.find(gameResultLong);
        if (!optionalGameResult.isPresent()) {
            throw new Exception("Could not find a game result with the given id");
        }

        String gameEndTime = optionalGameResult.get().getGameEndTime().format(dateTimeFormatter);

        Map map = new HashMap();
        map.put("gameresult", optionalGameResult.get());
        map.put("gameEndTime", gameEndTime);

        return freemarkerService.parseTemplate("gameEnd", map);
    }

    @MessageMapping("/requestVoteAction")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<String> requestVoteAction(Principal principal) throws Exception {
        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            throw new Exception("Could not find a user with the given nickname");
        }

        Map map = new HashMap();
        map.put("lobby", optionalUser.get().getLobby());
        map.put("userid", optionalUser.get().getId());
        map.put("vote", optionalUser.get().getPlayer().getVote());

        return freemarkerService.parseTemplate("gameVote", map);
    }

    @MessageMapping("/requestGame")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<String> requestGame(Principal principal) throws Exception {
        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            throw new Exception("Could not find a user with the given nickname");
        }

        Map map = new HashMap();

        if (optionalUser.get().getPlayer().isRealInsomniac()) {
            PlayerRole playerRole = playerRoleService.getRole(optionalUser.get().getPlayer().getRoleId());
            map.put("secretMessage", "You wake up as the " + playerRole.getName());
        }

        return freemarkerService.parseTemplate("game", map);
    }

    @MessageMapping("/requestNightAction")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<String> requestNightAction(Principal principal) throws Exception {
        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            throw new Exception("Could not find a user with the given nickname");
        }

        if (optionalUser.get().getLobby() == null || optionalUser.get().getLobby().getState() < 2) {
            throw new Exception("You can only view this page when you're in a game");
        }

        Map map = new HashMap();
        map.put("lobby", optionalUser.get().getLobby());
        map.put("userid", optionalUser.get().getId());
        int actionsPerformed = optionalUser.get().getPlayer().getActionsPerformed();

        NightAction[] nightActions = playerRoleService.getRole(optionalUser.get().getPlayer().getRoleId()).getNightActions(optionalUser.get().getLobby());

        if (actionsPerformed < nightActions.length) {
            map.put("nightAction", nightActions[actionsPerformed]);
        }

        return freemarkerService.parseTemplate("gameNightAction", map);
    }

    @MessageMapping("/requestCreateLobby")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<String> requestCreateLobby(Principal principal) throws Exception {
        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            throw new Exception("Could not find a user with the given nickname");
        }

        return freemarkerService.parseTemplate("createLobby", null);
    }

    @MessageMapping("/requestLobby/{lobbyId}")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<String> requestLobby(@DestinationVariable String lobbyId, Principal principal) throws Exception {
        if (lobbyId == null) {
            throw new Exception("Invalid lobbyId");
        }

        if (lobbyId.length() < 1) {
            throw new Exception("lobbyId has invalid length");
        }

        Integer lobbyInt = null;
        try {
            lobbyInt = Integer.parseInt(lobbyId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (lobbyInt == null || lobbyInt < 1) {
            throw new Exception("Bad lobbyInt");
        }

        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            throw new Exception("Could not find a user with the given nickname");
        }

        Optional<Lobby> optionalLobby = lobbyService.findLobbyById(lobbyInt);

        if (!optionalLobby.isPresent()) {
            throw new Exception("could not find a lobby with the given id");
        }

        Map map = new HashMap();
        map.put("lobby", optionalLobby.get());
        map.put("user", optionalUser.get());

        return freemarkerService.parseTemplate("lobby", map);
    }

    @MessageMapping("/requestRoleReveal")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<String> requestRoleReveal(Principal principal) throws Exception {
        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            throw new Exception("Could not find a user with the given nickname");
        }

        if (optionalUser.get().getPlayer() == null) {
            throw new Exception("Can't reveal role when user has no role!");
        }

        Map map = new HashMap();
        map.put("roleName", playerRoleService.getRole(optionalUser.get().getPlayer().getRoleId()).getName());

        return freemarkerService.parseTemplate("gameRoleReveal", map);
    }

    // @MessageMapping("/joinLobby")
    // @SendTo(value = "/endpoint/broadcast")
    @MessageMapping("/joinLobby")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<String> joinLobby(WebSocketRequestMessage<String> webSocketRequestMessage, Principal principal) throws Exception {
        String lobbyIdString = webSocketRequestMessage.getValue();
        Integer lobbyId = null;
        try {
            lobbyId = Integer.parseInt(lobbyIdString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (lobbyId == null) {
            return errorResponse("boardId can't be null");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            return errorResponse("could not find the user this message came from");
        }

        Optional<Lobby> optionalLobby = lobbyService.joinLobby(lobbyId, optionalUser.get());
        if (!optionalLobby.isPresent()) {
            return errorResponse("failed to join the lobby");
        }

        if (optionalLobby.get().getState() == 1) {
            Map map = new HashMap();
            map.put("lobby", optionalLobby.get());
            map.put("user", optionalUser.get());

            return freemarkerService.parseTemplate("lobby", map);
        } else {
            WebSocketResponseMessage<String> webSocketResponseMessage = new WebSocketResponseMessage<>();
            webSocketResponseMessage.setAction("nothing");
            webSocketResponseMessage.setContent("success");
            webSocketResponseMessage.setStatus(200);

            return webSocketResponseMessage;
        }
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
    @SendTo("/endpoint/broadcast")
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
                    //case "doppelganger":
                    //    lobby.addRole(Roles.DOPPELGANGER);
                    //    break;
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
        lobby.setState(1);

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

    // @SendToUser(value = "/endpoint/private")
    @MessageMapping("/voteAction")
    @SendTo(value = "/endpoint/broadcast")
    public WebSocketResponseMessage<String> voteAction(WebSocketRequestMessage<String> webSocketRequestMessage, Principal principal) throws Exception {
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

        return performVoteAction(optionalUser.get(), webSocketRequestMessage.getValue());
    }

    @MessageMapping("/hunterKill")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<String> hunterKill(WebSocketRequestMessage<String> webSocketRequestMessage, Principal principal) throws Exception {
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

        if (!(playerRoleService.getRole(optionalUser.get().getPlayer().getRoleId()) instanceof Hunter)) {
            throw new Exception("This role can't perform a hunter kill");
        }

        optionalUser.get().getLobby().setHunterKill(webSocketRequestMessage.getValue());
        lobbyService.save(optionalUser.get().getLobby());

        WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
        webSocketResponseMessage.setAction("hunterKill");
        webSocketResponseMessage.setContent(webSocketRequestMessage.getValue());
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
                    return performTroublemakerAction(user, optionalTargetUser.get(), user.getPlayer().getActionsPerformed(), messageValue);
                case 4: // Robber
                    return performRobberAction(user, optionalTargetUser.get(), user.getPlayer().getActionsPerformed(), messageValue);
                case 5: // Seer
                    return performSeerAction(user, optionalTargetUser.get(), user.getPlayer().getActionsPerformed(), messageValue);
                /*
                case 10: // Doppelganger
                    return performDoppelgangerAction(user, optionalTargetUser.get(), user.getPlayer().getActionsPerformed(), messageValue);
                */
                default: // Something went wrong
                    logger.warn("A user with role id " + roleID + " tried to do action " + messageValue);
                    throw new Exception("Someone tried to perform an action with a role that can't perform this action");
            }

            // Target is a neutral card
        } else if (messageValue.charAt(0) == 'n') {
            int targetNeutralId = Integer.parseInt(messageValue.substring(1));

            switch (roleID) {
                case 1: // Drunk
                    return performDrunkAction(user, user.getPlayer().getActionsPerformed(), messageValue);
                case 5: // Seer
                    return performSeerAction(user, targetNeutralId, user.getPlayer().getActionsPerformed(), messageValue);
                /*
                case 10: // Doppelganger
                    return performDoppelgangerAction(user, targetNeutralId, user.getPlayer().getActionsPerformed(), messageValue);
                */
                case 14: // Werewolf 1
                case 15: // Werewolf 2
                    return performWerewolfAction(user, user.getPlayer().getActionsPerformed(), messageValue);
                default: // Something went wrong
                    logger.warn("A user with role id " + roleID + " tried to do action " + messageValue);
                    throw new Exception("Someone tried to perform an action with a role that can't perform this action");
            }
        } else {
            throw new Exception("Could not understand what target you chose");
        }

        // throw new Exception("Something went wrong");
    }

    private WebSocketResponseMessage<String> performVoteAction(User user, String messageValue) throws Exception {
        int targetUserId = Integer.parseInt(messageValue.substring(1));

        Optional<User> optionalTargetUser = userService.findById(targetUserId);
        if (!optionalTargetUser.isPresent()) {
            throw new Exception("Could not find a user with the given target id");
        }

        if (user.getLobby().getId() != optionalTargetUser.get().getLobby().getId()) {
            throw new Exception("Someone tried to vote for a user that isn't in their lobby");
        }

        PlayerVoteAction playerVoteAction = new PlayerVoteAction();

        if (user.getPlayer().getVote() != null && user.getPlayer().getVote().length() > 0) {
            int previousVoteUserId = Integer.parseInt(user.getPlayer().getVote().substring(1));
            Optional<User> optionalPreviousVoteUser = userService.findById(previousVoteUserId);

            playerVoteAction.setPreviousVote(user.getPlayer().getVote());
            optionalPreviousVoteUser.get().getPlayer().setVotesAgainstPlayer(optionalPreviousVoteUser.get().getPlayer().getVotesAgainstPlayer() - 1);
            playerService.save(optionalPreviousVoteUser.get().getPlayer());
            playerVoteAction.setPreviousVotes(optionalTargetUser.get().getPlayer().getVotesAgainstPlayer());
        }

        if (user.getPlayer().getVote() != null && user.getPlayer().getVote().length() > 0 && user.getPlayer().getVote().equals(messageValue)) {
            user.getPlayer().setVote(null);
            optionalTargetUser.get().getPlayer().setVotesAgainstPlayer(optionalTargetUser.get().getPlayer().getVotesAgainstPlayer() - 1);
            playerVoteAction.setVoteIndicator("-");
        } else {
            user.getPlayer().setVote(messageValue);
            optionalTargetUser.get().getPlayer().setVotesAgainstPlayer(optionalTargetUser.get().getPlayer().getVotesAgainstPlayer() + 1);
            playerVoteAction.setVoteIndicator("+");
        }

        userService.save(user);
        playerService.save(optionalTargetUser.get().getPlayer());

        playerVoteAction.setVoter(user.getNickname());
        playerVoteAction.setVotedFor("u" + optionalTargetUser.get().getId());
        playerVoteAction.setVotes(optionalTargetUser.get().getPlayer().getVotesAgainstPlayer());

        WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
        webSocketResponseMessage.setAction("updateVotes");
        webSocketResponseMessage.setContent(playerVoteAction);
        webSocketResponseMessage.setStatus(200);

        return webSocketResponseMessage;
    }

    private WebSocketResponseMessage<String> performTroublemakerAction(User user, User target, int actionsPerformed, String messageValue) throws Exception {
        if (actionsPerformed == 1) {
            user.getPlayer().setActionsPerformed(user.getPlayer().getActionsPerformed() + 1);
            user.getPlayer().setNightActionTargetTwo(messageValue);
            playerService.save(user.getPlayer());

            return freemarkerService.parseTemplate("gameNightAction", null);
        } else if (actionsPerformed == 0) {
            user.getPlayer().setActionsPerformed(user.getPlayer().getActionsPerformed() + 1);
            user.getPlayer().setNightActionTarget(messageValue);
            playerService.save(user.getPlayer());

            Map map = new HashMap();
            map.put("smallMessage", "Swapping " + target.getNickname() + " with...");
            map.put("nightAction", playerRoleService.getRole(user.getPlayer().getRoleId()).getNightActions(user.getLobby())[actionsPerformed]);
            map.put("firstTarget", messageValue);
            map.put("userid", user.getId());
            map.put("lobby", user.getLobby());

            return freemarkerService.parseTemplate("gameNightAction", map);
        } else {
            throw new Exception("You have already performed all your actions");
        }
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

        return freemarkerService.parseTemplate("gameNightAction", map);
    }

    private WebSocketResponseMessage<String> performSeerAction(User user, int targetNeutralId, int actionsPerformed, String messageValue) throws Exception {
        if (targetNeutralId < 1 || targetNeutralId > 3) {
            throw new IllegalArgumentException("targetNeutralId can't be greater than 3 or less than 1");
        }

        if (actionsPerformed == 0) {
            user.getPlayer().setActionsPerformed(user.getPlayer().getActionsPerformed() + 1);
            user.getPlayer().setNightActionTarget(messageValue);
            playerService.save(user.getPlayer());

            Map map = new HashMap();
            switch (targetNeutralId) {
                case 1:
                    map.put("viewRole", playerRoleService.getRole(user.getLobby().getNeutralOne()).getName());
                    break;
                case 2:
                    map.put("viewRole", playerRoleService.getRole(user.getLobby().getNeutralTwo()).getName());
                    break;
                case 3:
                    map.put("viewRole", playerRoleService.getRole(user.getLobby().getNeutralThree()).getName());
                    break;
            }

            map.put("nightAction", playerRoleService.getRole(user.getPlayer().getRoleId()).getNightActions(user.getLobby())[1]);
            map.put("firstTarget", messageValue);
            map.put("userid", user.getId());
            map.put("lobby", user.getLobby());

            return freemarkerService.parseTemplate("gameNightAction", map);
        } else if (actionsPerformed == 1 && user.getPlayer().getNightActionTarget() != null && user.getPlayer().getNightActionTarget().length() > 0 && user.getPlayer().getNightActionTarget().charAt(0) == 'n') {
            user.getPlayer().setActionsPerformed(user.getPlayer().getActionsPerformed() + 1);
            user.getPlayer().setNightActionTarget(messageValue);
            playerService.save(user.getPlayer());

            Map map = new HashMap();
            switch (targetNeutralId) {
                case 1:
                    map.put("viewRole", playerRoleService.getRole(user.getLobby().getNeutralOne()).getName());
                    break;
                case 2:
                    map.put("viewRole", playerRoleService.getRole(user.getLobby().getNeutralTwo()).getName());
                    break;
                case 3:
                    map.put("viewRole", playerRoleService.getRole(user.getLobby().getNeutralThree()).getName());
                    break;
            }

            return freemarkerService.parseTemplate("gameNightAction", map);
        } else {
            throw new Exception("You have already performed all your actions");
        }
    }

    private WebSocketResponseMessage<String> performSeerAction(User user, User target, int actionsPerformed, String messageValue) throws Exception {
        if (actionsPerformed > 0) {
            throw new Exception("You have already performed all your actions");
        }

        user.getPlayer().setActionsPerformed(user.getPlayer().getActionsPerformed() + 1);
        user.getPlayer().setNightActionTarget(messageValue);
        playerService.save(user.getPlayer());

        Map map = new HashMap();
        map.put("viewRole", playerRoleService.getRole(target.getPlayer().getRoleId()).getName());

        return freemarkerService.parseTemplate("gameNightAction", null);
    }

    private WebSocketResponseMessage<String> performDrunkAction(User user, int actionsPerformed, String messageValue) throws Exception {
        if (actionsPerformed > 0) {
            throw new Exception("You have already performed all your actions");
        }

        user.getPlayer().setActionsPerformed(user.getPlayer().getActionsPerformed() + 1);
        user.getPlayer().setNightActionTarget(messageValue);
        playerService.save(user.getPlayer());

        return freemarkerService.parseTemplate("gameNightAction", null);
    }

    private WebSocketResponseMessage<String> performWerewolfAction(User user, int actionsPerformed, String messageValue) throws Exception {
        if (actionsPerformed > 0) {
            throw new Exception("You have already performed all your actions");
        }

        if (!user.getLobby().isLoneWolf()) {
            throw new Exception("You can only perform this action if you're the lone wolf!");
        }

        if (messageValue == null || messageValue.length() < 2) {
            throw new Exception("Night action targets can't be less than 2 characters in length");
        }

        user.getPlayer().setActionsPerformed(user.getPlayer().getActionsPerformed() + 1);
        user.getPlayer().setNightActionTarget(messageValue);
        playerService.save(user.getPlayer());

        Integer roleId = null;

        switch(messageValue.substring(1)) {
            case "1":
                roleId = user.getLobby().getNeutralOne();
                break;
            case "2":
                roleId = user.getLobby().getNeutralTwo();
                break;
            case "3":
                roleId = user.getLobby().getNeutralThree();
                break;
        }

        // TODO: This should log a warning and then return something useful instead of throwing an exception
        if (roleId == null) {
            throw new Exception("performWerewolfAction somehow got a null roleId");
        }

        Map map = new HashMap();
        map.put("viewRole", playerRoleService.getRole(roleId).getName());

        return freemarkerService.parseTemplate("gameNightAction", map);
    }

    /*
    private WebSocketResponseMessage<String> performDoppelgangerAction(User user, int targetNeutralId, int actionsPerformed, String messageValue) throws Exception {
        if (actionsPerformed == 1) {
            user.getPlayer().setActionsPerformed(0);
            int targetUserId = Integer.parseInt(messageValue.substring(1));
            Optional<User> optionalTargetUser = userService.findById(targetUserId);
            if (!optionalTargetUser.isPresent()) {
                throw new Exception("Could not find a user with the given target id");
            }

            return performNightAction(user, messageValue, optionalTargetUser.get().getPlayer().getRoleId());
        } else {
            throw new Exception("You have already performed all your actions");
        }
    }

    private WebSocketResponseMessage<String> performDoppelgangerAction(User user, User target, int actionsPerformed, String messageValue) throws Exception {
        if (actionsPerformed == 0 && user.getPlayer().getDoppelgangerTargetId() == null) {
            // user.getPlayer().setActionsPerformed(user.getPlayer().getActionsPerformed() + 1);
            // user.getPlayer().setNightActionTarget(messageValue);
            PlayerRole targetRole = playerRoleService.getRole(target.getPlayer().getRoleId());

            user.getPlayer().setRoleId(target.getPlayer().getRoleId());
            user.getPlayer().setDoppelgangerTargetId(target.getId());
            playerService.save(user.getPlayer());

            NightAction[] nightActions;
            if (targetRole instanceof Werewolf) {
                nightActions = new NightAction[]{};
            } else {
                nightActions = targetRole.getNightActions(user.getLobby());
            }

            Map map = new HashMap();
            map.put("viewRole", targetRole.getName());
            map.put("userid", user.getId());
            map.put("lobby", user.getLobby());

            if (nightActions.length > 0) {
                map.put("nightAction", nightActions[0]);
            }

            return freemarkerService.parseTemplate("gameNightAction", map);
        } else {
            throw new Exception("You have already performed all your actions");
        }
    }
    */
}
