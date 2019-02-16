package lycanthrope.controllers;

import lycanthrope.models.Lobby;
import lycanthrope.models.User;
import lycanthrope.services.LobbyService;
import lycanthrope.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.Optional;

@Controller
public class LobbyController {

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/lobbies")
    public String getMainPage(Principal principal, Model model) throws Exception {
        // As long as our SecurityConfig works as intended this will never be true
        if (principal == null) {
            throw new Exception("Unauthorized");
        }

        Optional<User> optionalUser = userService.findByNickname(principal.getName());
        if (!optionalUser.isPresent()) {
            throw new Exception("Could not find a user with the given nickname");
        }

        model.addAttribute("nickname", optionalUser.get().getNickname());

        return "lobbiesIndex";
    }

    @GetMapping("/lobby/{lobbyId}")
    public String getLobby(@PathVariable(name = "lobbyId") Integer lobbyId, Model model) throws Exception {
        Optional<Lobby> optionalLobby = lobbyService.findLobbyById(lobbyId);

        if (!optionalLobby.isPresent()) {
            throw new Exception("could not find a lobby with the given id");
        }

        model.addAttribute("lobby", optionalLobby.get());

        return "lobby";
    }
}
