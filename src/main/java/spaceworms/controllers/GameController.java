package spaceworms.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GameController {

    @GetMapping("/game/{lobbyId}")
    public String getGame() {
        return "game";
    }
}
