package lycanthrope.controllers;

import lycanthrope.models.GameResult;
import lycanthrope.models.NightAction;
import lycanthrope.models.PlayerRole;
import lycanthrope.models.User;
import lycanthrope.models.roles.Insomniac;
import lycanthrope.services.GameResultService;
import lycanthrope.services.PlayerRoleService;
import lycanthrope.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
public class GameController {

    @Autowired
    private UserService userService;

    @Autowired
    private PlayerRoleService playerRoleService;

    @Autowired
    private GameResultService gameResultService;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /*
    @GetMapping("/game")
    public String getGame(Model model, Principal principal) throws Exception {
        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            throw new Exception("Could not find a user with the given nickname");
        }

        if (optionalUser.get().getPlayer().isRealInsomniac()) {
            PlayerRole playerRole = playerRoleService.getRole(optionalUser.get().getPlayer().getRoleId());
            model.addAttribute("secretMessage", "You wake up as the " + playerRole.getName());
        }

        return "game";
    }
    */

    @GetMapping("/day/{lobbyId}")
    public String getDay(@PathVariable(name = "lobbyId") Integer lobbyId, Model model, Principal principal) throws Exception {
        return "gameVote";
    }

    /*
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
    */

    /*
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
    */

    /*
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

        String gameEndTime = optionalGameResult.get().getGameEndTime().format(dateTimeFormatter);

        model.addAttribute("gameresult", optionalGameResult.get());
        model.addAttribute("gameEndTime", gameEndTime);

        return "gameEnd";
    }
    */

    /*
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
    */
}
