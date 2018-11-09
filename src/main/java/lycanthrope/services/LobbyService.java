package lycanthrope.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lycanthrope.models.*;
import lycanthrope.repositories.LobbyRepository;

import java.util.List;
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
    private UserService userService;

    private Random random = new Random();

    public List<Lobby> getAllLobbies() {
        return lobbyRepository.findAll();
    }

    public Optional<Lobby> findLobbyById(int boardId) {
        return lobbyRepository.findById(boardId);
    }

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
                    lobby = new Lobby();
                    lobby.setId(boardId);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            createLobbySemaphore.release();
        }

        if (user.getLobby() != null && user.getLobby().getId() == lobby.getId()) {
            return Optional.of(lobby);
        }

        boolean success = false;
        try {
            joinLobbySemaphore.acquire();

            if (lobby.getCurrentPlayerSize() < lobby.getLobbyMaxSize()) {
                lobby.addUser(user);

                if (lobby.getCurrentPlayerSize() == lobby.getLobbyMaxSize()) {
                    lobby.setStarted(true);
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

    public void delete(Lobby lobby) {
        lobbyRepository.delete(lobby);
    }


    public void save(Lobby lobby) {
        lobbyRepository.save(lobby);
    }

    private int getRandomDieThrow() {
        return random.nextInt(6) + 1;
    }
}