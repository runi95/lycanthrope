package spaceworms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import spaceworms.models.Board;
import spaceworms.models.Message;
import spaceworms.services.APIService;

import java.util.List;
import java.util.Optional;

@Controller
public class WebSocketController {

    @Autowired
    APIService api;

    @MessageMapping("/getBoards")
    @SendToUser(value = "/endpoint/private")
    public Message<List<Board>> getBoards() {
        Optional<List<Board>> optionalBoards = api.getBoards(null);
        Message<List<Board>> message = new Message<>();

        if (optionalBoards.isPresent()) {
            message.setContent(optionalBoards.get());
            message.setStatus(200);
        } else {
            message.setContent(null);
            message.setStatus(500);
        }

        return message;
    }
}
