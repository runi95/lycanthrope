package lycanthrope.controllers;

import lycanthrope.models.Lobby;
import lycanthrope.models.User;
import lycanthrope.services.LobbyService;
import lycanthrope.services.PrincipalService;
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
    private PrincipalService principalService;

    @GetMapping(value = "/lobbies")
    public String getMainPage(Principal principal, Model model) throws Exception {
        User user = principalService.getUserFromPrincipal(principal);

        model.addAttribute("nickname", user.getNickname());

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
