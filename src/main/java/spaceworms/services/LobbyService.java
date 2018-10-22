package spaceworms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spaceworms.models.*;
import spaceworms.repositories.LobbyRepository;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Semaphore;

@Service
public class LobbyService {

    private final Semaphore joinLobbySemaphore = new Semaphore(1);
    private final Semaphore createLobbySemaphore = new Semaphore(1);

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private BoardService boardService;

    @Autowired
    private SquareService squareService;

    @Autowired
    private UserService userService;

    @Autowired
    private APIService apiService;

    private Random random = new Random();

    public Optional<Lobby> findLobbyById(int boardId) {
        return lobbyRepository.findById(boardId);
    }

    // TODO: Fix bug where the same player can join the same lobby twice
    public Optional<Lobby> joinLobby(int boardId, User user) {
        Lobby lobby = null;
        Optional<Lobby> optionalLobby = findLobbyById(boardId);

        if (optionalLobby.isPresent()) {
            lobby = optionalLobby.get();
        } else {
            try {
                createLobbySemaphore.acquire();

                // Maybe someone created the lobby while we were waiting for the semaphore?
                optionalLobby = findLobbyById(boardId);
                if (optionalLobby.isPresent()) {
                    lobby = optionalLobby.get();
                } else {
                    Optional<Board> optionalBoard = apiService.loadBoardAndSquares(boardId);
                    if (!optionalBoard.isPresent()) {
                        return Optional.empty();
                    }

                    boardService.save(optionalBoard.get());

                    lobby = new Lobby();
                    lobby.setId(boardId);
                    lobby.setBoard(optionalBoard.get());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            createLobbySemaphore.release();
        }

        boolean success = false;
        try {
            joinLobbySemaphore.acquire();

            if (lobby.getLobbySize() < 4) {
                user.setPlayerNumber(lobby.getLobbySize() + 1);
                userService.save(user);

                lobby.addUser(user);

                if (lobby.getUsers().size() == 1) {
                    lobby.setStarted(true);
                    int startPosition = lobby.getBoard().getStart();
                    lobby.getUsers().forEach(u -> { u.setSquareNumber(startPosition); userService.save(u); });
                }

                lobbyRepository.save(lobby);

                success = true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        joinLobbySemaphore.release();

        if (success) {
            return Optional.of(lobby);
        } else {
            return Optional.empty();
        }
    }

    public DiceThrowResult rollDice(User user) {
        Lobby lobby = user.getLobby();
        int dieResult = getRandomDieThrow();
        int landingSquareNumber = user.getSquareNumber() + dieResult;

        Square landingSquare = lobby.getBoard().getSquares().get(Math.min(landingSquareNumber, lobby.getBoard().getDimX() * lobby.getBoard().getDimY() - 1));
        if (landingSquare.isWormhole()) {
            user.setSquareNumber(landingSquare.getWormhole() - 1);
        } else {
            user.setSquareNumber(landingSquareNumber);
        }

        DiceThrowResult diceThrowResult = new DiceThrowResult();
        diceThrowResult.setDiceThrowLandingSquare(landingSquareNumber);
        diceThrowResult.setDiceThrowResult(dieResult);
        diceThrowResult.setLastPlayerId(lobby.getCurrentPlayerId());

        boolean userWon = user.getSquareNumber() == lobby.getBoard().getGoal();
        if (userWon) {
            diceThrowResult.setWinningPlayerId(user.getPlayerNumber());
            diceThrowResult.setGameEnded(true);
        }

        if (userWon) {
            lobbyRepository.delete(lobby);
        } else {
            userService.save(user);

            int nextPlayerId = (lobby.getCurrentPlayerId() % lobby.getUsers().size()) + 1;
            diceThrowResult.setNextPlayerId(nextPlayerId);

            lobby.setCurrentPlayerId(nextPlayerId);
            lobbyRepository.save(lobby);
        }

        return diceThrowResult;
    }

    public int getRandomDieThrow() {
        return random.nextInt(6) + 1;
    }
}