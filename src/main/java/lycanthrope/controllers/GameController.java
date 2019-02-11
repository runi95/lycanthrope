package lycanthrope.controllers;

import lycanthrope.models.NightAction;
import lycanthrope.models.Roles;
import lycanthrope.models.User;
import lycanthrope.services.PlayerRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import lycanthrope.services.LobbyService;
import lycanthrope.services.UserService;

import java.security.Principal;
import java.util.Optional;

@Controller
public class GameController {

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlayerRoleService playerRoleService;

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

    @GetMapping("/vote")
    public String getVote() {
        return "gameVote";
    }
}
