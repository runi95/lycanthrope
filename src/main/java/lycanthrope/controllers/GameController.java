package lycanthrope.controllers;

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

@Controller
public class GameController {

    @Autowired
    private UserService userService;

    @Autowired
    private PlayerRoleService playerRoleService;

    @Autowired
    private GameResultService gameResultService;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @GetMapping("/day/{lobbyId}")
    public String getDay(@PathVariable(name = "lobbyId") Integer lobbyId, Model model, Principal principal) throws Exception {
        return "gameVote";
    }
}
