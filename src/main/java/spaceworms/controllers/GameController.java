package spaceworms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import spaceworms.models.Lobby;
import spaceworms.models.User;
import spaceworms.services.LobbyService;
import spaceworms.services.UserService;

import java.security.Principal;
import java.util.Optional;

@Controller
public class GameController {

    @Autowired
    LobbyService lobbyService;

    @Autowired
    UserService userService;

    @GetMapping("/game/{lobbyId}")
    public String getGame(@PathVariable(name = "lobbyId") Integer lobbyId, Model model, Principal principal) throws Exception {
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

        return "game";
    }
}
