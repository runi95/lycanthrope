package lycanthrope.controllers;

import lycanthrope.models.*;
import lycanthrope.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class GameController {

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlayerRoleService playerRoleService;

    @Autowired
    private GameResultService gameResultService;

    @Autowired
    private GameResultPlayerService gameResultPlayerService;

    @GetMapping("/game")
    public String getGame(Model model, Principal principal) throws Exception {
        /*
        if (lobbyId == null) {
            throw new Exception("lobbyId can't be null");
        }

        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            throw new Exception("Could not find a user with the given nickname");
        }

        Optional<Lobby> optionalLobby = lobbyService.findLobbyById(lobbyId);
        if (!optionalLobby.isPresent()) {
            throw new Exception("Could not find any lobbies with the given lobbyId");
        }

        model.addAttribute("user", optionalUser.get());
        model.addAttribute("lobby", optionalLobby.get());
        */

        return "game";
    }

    @GetMapping("/day/{lobbyId}")
    public String getDay(@PathVariable(name = "lobbyId") Integer lobbyId, Model model, Principal principal) throws Exception {
        /*
        if (lobbyId == null) {
            throw new Exception("lobbyId can't be null");
        }

        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            throw new Exception("Could not find a user with the given nickname");
        }

        Optional<Lobby> optionalLobby = lobbyService.findLobbyById(lobbyId);
        if (!optionalLobby.isPresent()) {
            throw new Exception("Could not find any lobbies with the given lobbyId");
        }

        model.addAttribute("user", optionalUser.get());
        model.addAttribute("lobby", optionalLobby.get());
        */

        return "gameVote";
    }

    @GetMapping("/game/{lobbyId}/roleReveal")
    public String getRole(Principal principal, Model model) throws Exception {
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

        model.addAttribute("roleName", playerRoleService.getRole(optionalUser.get().getPlayer().getRoleId()).getName());

        return "gameRoleReveal";
    }

    @GetMapping("/nightAction")
    public String getNightAction(Model model, Principal principal) throws Exception {
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

        model.addAttribute("lobby", optionalUser.get().getLobby());
        model.addAttribute("userid", optionalUser.get().getId());
        int actionsPerformed = optionalUser.get().getPlayer().getActionsPerformed();

        NightAction[] nightActions = playerRoleService.getRole(optionalUser.get().getPlayer().getRoleId()).getNightActions(optionalUser.get().getLobby());

        if (actionsPerformed < nightActions.length) {
            model.addAttribute("nightAction", nightActions[actionsPerformed]);
        }

        return "gameNightAction";
    }

    @GetMapping("/result/{gameResultId}")
    public String getVote(@PathVariable("gameResultId") Long gameResultId, Principal principal, Model model) throws Exception {
        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        if (gameResultId == null || gameResultId < 1) {
            throw new Exception("Invalid gameResultId");
        }

        Optional<GameResult> optionalGameResult = gameResultService.find(gameResultId);
        if (!optionalGameResult.isPresent()) {
            throw new Exception("Could not find a game result with the given id");
        }

        model.addAttribute("gameresult", optionalGameResult.get());

        /*
        Set<GameResultPlayer> gameResultPlayers = new HashSet<>();
        GameResultPlayer one = new GameResultPlayer();
        one.setId(1);
        one.setDead(true);
        one.setNickname("Night Twister");
        one.setRole("WEREWOLF");
        one.setWinner(false);

        GameResultPlayer two = new GameResultPlayer();
        two.setDead(false);
        two.setNickname("Promises");
        two.setRole("HUNTER");
        two.setWinner(true);
        two.setId(2);

        GameResultPlayer three = new GameResultPlayer();
        three.setDead(false);
        three.setNickname("C2H60");
        three.setRole("INSOMNIAC");
        three.setWinner(true);
        three.setId(3);

        GameResultPlayer four = new GameResultPlayer();
        four.setDead(false);
        four.setNickname("Janne");
        four.setRole("DRUNK");
        four.setWinner(true);
        four.setId(4);

        GameResultPlayer five = new GameResultPlayer();
        five.setDead(false);
        five.setNickname("Bokki");
        five.setRole("MINION");
        five.setWinner(false);
        five.setId(5);

        gameResultPlayers.add(one);
        gameResultPlayers.add(two);
        gameResultPlayers.add(three);
        gameResultPlayers.add(four);
        gameResultPlayers.add(five);

        GameResult gameResult = new GameResult();
        gameResult.setGameEndTime(LocalDateTime.now());
        gameResult.setGameResultPlayers(gameResultPlayers);

        model.addAttribute("gameresult", gameResult);
        */

        return "gameEnd";
    }

    @GetMapping("/voteAction")
    public String getVoteAction(Principal principal, Model model) throws Exception {
        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            throw new Exception("Could not find a user with the given nickname");
        }

        model.addAttribute("lobby", optionalUser.get().getLobby());
        model.addAttribute("userid", optionalUser.get().getId());
        model.addAttribute("vote", optionalUser.get().getPlayer().getVote());

        return "gameVote";
    }
}
