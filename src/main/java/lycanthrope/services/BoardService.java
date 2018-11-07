package lycanthrope.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lycanthrope.models.Board;
import lycanthrope.repositories.BoardRepository;

import java.util.List;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    public void save(Board board) {
        boardRepository.save(board);
    }

    public void saveAll(List<Board> boardList) {
        boardRepository.saveAll(boardList);
    }

}
