package spaceworms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spaceworms.models.Square;
import spaceworms.repositories.SquareRepository;

import java.util.List;

@Service
public class SquareService {

    @Autowired
    private SquareRepository squareRepository;

    public void save(Square square) {
        squareRepository.save(square);
    }

    public void saveAll(List<Square> squareList) {
        squareRepository.saveAll(squareList);
    }
}
