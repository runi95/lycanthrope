package spaceworms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spaceworms.authentication.UserPrincipal;
import spaceworms.models.Board;
import spaceworms.models.User;
import spaceworms.services.APIService;

import javax.servlet.http.HttpServletRequest;
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
