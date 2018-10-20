package spaceworms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import spaceworms.models.Board;
import spaceworms.services.APIService;

import java.util.Optional;

@Controller
public class BoardController {

    @Autowired
    APIService apiService;

    @GetMapping("/boards")
    public String getBoards() {
        return "boards";
    }

    @GetMapping("/boards/{boardId}")
    public String getBoard(@PathVariable("boardId") Integer boardId, Model model) {
        Optional<Board> optionalBoard = apiService.getBoard(boardId);

        if (optionalBoard.isPresent()) {
            model.addAttribute("board", optionalBoard.get());
        }

        return "boardLobby";
    }
}
