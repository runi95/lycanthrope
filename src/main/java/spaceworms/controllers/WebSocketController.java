package spaceworms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import spaceworms.models.Board;
import spaceworms.models.Message;
import spaceworms.services.API;

import java.util.List;

@Controller
public class WebSocketController {

    @Autowired
    API api;

    @MessageMapping("/getBoards")
    @SendToUser(value = "/endpoint/private")
    public Message<List<Board>> getBoards() {

        // TODO: Find a better way to handle the api, maybe a wrapper service that returns messages?
        Message<List<Board>> message = new Message<>();
        message.setContent(api.getBoards());
        message.setStatus(200);

        return message;
    }
}
