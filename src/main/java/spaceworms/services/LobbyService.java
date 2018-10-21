package spaceworms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spaceworms.models.Board;
import spaceworms.models.Lobby;
import spaceworms.models.User;
import spaceworms.repositories.LobbyRepository;

import java.util.Optional;
import java.util.concurrent.Semaphore;

@Service
public class LobbyService {

    Semaphore joinLobbySemaphore = new Semaphore(1);
    Semaphore createLobbySemaphore = new Semaphore(1);

    @Autowired
    LobbyRepository lobbyRepository;

    @Autowired
    APIService apiService;

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
                    Optional<Board> optionalBoard = apiService.getBoard(boardId);
                    if (!optionalBoard.isPresent()) {
                        return Optional.empty();
                    }

                    lobby = new Lobby();
                    lobby.setId(boardId);
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
                lobby.addUser(user);
                lobbyRepository.save(lobby);

                if (lobby.getUsers().size() == 4) {
                    lobby.setStarted(true);
                }

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
}