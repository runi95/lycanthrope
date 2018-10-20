package spaceworms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import spaceworms.models.*;
import spaceworms.services.APIService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class WebSocketController {

    @Autowired
    APIService api;

    @MessageMapping("/getBoards")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<List<Board>> getBoards(Principal principal) {
        Optional<List<Board>> optionalBoards = api.getBoards(null);
        WebSocketResponseMessage<List<Board>> message = new WebSocketResponseMessage<>();

        if (optionalBoards.isPresent()) {
            message.setContent(optionalBoards.get());
            message.setAction("populateboardtable");
            message.setStatus(200);
        } else {
            message.setContent(null);
            message.setStatus(500);
        }

        return message;
    }

    @MessageMapping("/joinLobby")
    @SendTo(value = "/endpoint/broadcast")
    public WebSocketResponseMessage<String> joinLobby() {
        return null;
    }

    @MessageMapping("/getLobby")
    @SendToUser(value = "/endpoint/private")
    public WebSocketResponseMessage<List<Player>> getLobby() {
        return null;
    }
}
