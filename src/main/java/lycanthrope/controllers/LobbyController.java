package lycanthrope.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LobbyController {

    @GetMapping(value = "/lobbies")
    public String getMainPage() {
        return "lobbies";
    }

    @GetMapping("/createLobby")
    public String getCreateLobby() {
        return "createLobby";
    }
}
