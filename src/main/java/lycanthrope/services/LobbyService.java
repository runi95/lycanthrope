package lycanthrope.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import lycanthrope.models.*;
import lycanthrope.repositories.LobbyRepository;
import java.util.*;
import java.util.concurrent.Semaphore;

@Service
public class LobbyService {

    private final Semaphore joinLobbySemaphore = new Semaphore(1);
    private final Semaphore createLobbySemaphore = new Semaphore(1);

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    SimpMessagingTemplate simpTemplate;

    @Autowired
    private PlayerService playerService;

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
        boolean started = false;
        try {
            joinLobbySemaphore.acquire();

            if (lobby.getCurrentPlayerSize() < lobby.getLobbyMaxSize()) {
                lobby.addUser(user);

                if (lobby.getCurrentPlayerSize() == lobby.getLobbyMaxSize()) {
                    Collection<Roles> rolesCollection = lobby.getRoles();
                    ArrayList<Roles> rolesArrayList = new ArrayList<>();
                    rolesCollection.forEach(role -> rolesArrayList.add(role));

                    Collections.shuffle(rolesArrayList);
                    Iterator<Roles> iterator = rolesArrayList.iterator();

                    int wolves = 0;
                    for (User u : lobby.getUsers()) {
                        Player player = new Player();
                        Roles roles = iterator.next();
                        if (roles == Roles.WEREWOLF1 || roles == Roles.WEREWOLF2) {
                            wolves++;
                        }

                        player.setRoleId(roles.ordinal());
                        player.setUser(u);
                        u.setPlayer(player);

                        playerService.save(player);
                    }

                    lobby.setNeutralOne(iterator.next().ordinal());
                    lobby.setNeutralTwo(iterator.next().ordinal());
                    lobby.setNeutralThree(iterator.next().ordinal());

                    if (wolves == 1) {
                        lobby.setLoneWolf(true);
                    } else {
                        lobby.setLoneWolf(false);
                    }

                    started = true;
                    lobby.setState(2);
                }

                lobbyRepository.save(lobby);

                success = true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        joinLobbySemaphore.release();

        if (success) {
            if (started) {
                int lobbyId = lobby.getId();
                taskScheduler.schedule(() -> {
                    scheduleRoleReveal(lobbyId);
                }, new Date(System.currentTimeMillis() + 30000));
            }

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

    private void scheduleRoleReveal(int lobbyId) {
        Optional<Lobby> optionalLobby = findLobbyById(lobbyId);
        if (!optionalLobby.isPresent()) {
            return;
        }

        optionalLobby.get().setState(3);
        save(optionalLobby.get());

        WebSocketResponseMessage<String> webSocketResponseMessage = new WebSocketResponseMessage<>();
        webSocketResponseMessage.setStatus(200);
        webSocketResponseMessage.setAction("requestNightAction");
        webSocketResponseMessage.setContent("");

        simpTemplate.convertAndSend("/endpoint/broadcast", webSocketResponseMessage);
        taskScheduler.schedule(() -> {
            scheduleNightAction(lobbyId);
        }, new Date(System.currentTimeMillis() + 20000));
    }

    private void scheduleNightAction(int lobbyId) {
        Optional<Lobby> optionalLobby = findLobbyById(lobbyId);
        if (!optionalLobby.isPresent()) {
            return;
        }

        optionalLobby.get().setState(4);
        save(optionalLobby.get());

        User drunk = null;
        User troublemaker = null;
        User robber = null;

        for (User user : optionalLobby.get().getUsers()) {
            if (user.getPlayer().getRoleId() == 1) {
                drunk = user;
            } else if (user.getPlayer().getRoleId() == 3) {
                troublemaker = user;
            } else if (user.getPlayer().getRoleId() == 4) {
                robber = user;
            }
        }

        if (robber != null) {
            try {
                int robberTarget = Integer.parseInt(drunk.getPlayer().getNightActionTarget().substring(1));

                Optional<User> optionalRobberTargetUser = userService.findById(robberTarget);
                if (optionalRobberTargetUser.isPresent()) {
                    int robberNewRole = optionalRobberTargetUser.get().getPlayer().getRoleId();
                    int robberOldRole = robber.getPlayer().getRoleId();

                    robber.getPlayer().setRoleId(robberNewRole);
                    userService.save(robber);

                    optionalRobberTargetUser.get().getPlayer().setRoleId(robberOldRole);
                    userService.save(optionalRobberTargetUser.get());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (troublemaker != null) {
            // TODO: implement
        }

        if (drunk != null) {
            try {
                int drunkTarget = Integer.parseInt(drunk.getPlayer().getNightActionTarget().substring(1));

                if (drunkTarget == 1) {
                    int drunkNewRole = optionalLobby.get().getNeutralOne();
                    int drunkOldRole = drunk.getPlayer().getRoleId();

                    drunk.getPlayer().setRoleId(drunkNewRole);
                    playerService.save(drunk.getPlayer());

                    optionalLobby.get().setNeutralOne(drunkOldRole);
                } else if (drunkTarget == 2) {
                    int drunkNewRole = optionalLobby.get().getNeutralTwo();
                    int drunkOldRole = drunk.getPlayer().getRoleId();

                    drunk.getPlayer().setRoleId(drunkNewRole);
                    playerService.save(drunk.getPlayer());

                    optionalLobby.get().setNeutralTwo(drunkOldRole);
                } else if (drunkTarget == 3) {
                    int drunkNewRole = optionalLobby.get().getNeutralThree();
                    int drunkOldRole = drunk.getPlayer().getRoleId();

                    drunk.getPlayer().setRoleId(drunkNewRole);
                    playerService.save(drunk.getPlayer());

                    optionalLobby.get().setNeutralThree(drunkOldRole);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        save(optionalLobby.get());

        WebSocketResponseMessage<String> webSocketResponseMessage = new WebSocketResponseMessage<>();
        webSocketResponseMessage.setStatus(200);
        webSocketResponseMessage.setAction("requestGame");
        webSocketResponseMessage.setContent("");

        simpTemplate.convertAndSend("/endpoint/broadcast", webSocketResponseMessage);

        taskScheduler.schedule(() -> {
            scheduleVoteAction(lobbyId);
        }, new Date(System.currentTimeMillis() + 100000));
    }

    private void scheduleVoteAction(int lobbyId) {
        WebSocketResponseMessage<String> webSocketResponseMessage = new WebSocketResponseMessage<>();
        webSocketResponseMessage.setStatus(200);
        webSocketResponseMessage.setAction("requestVoteAction");
        webSocketResponseMessage.setContent("");

        simpTemplate.convertAndSend("/endpoint/broadcast", webSocketResponseMessage);
    }
}