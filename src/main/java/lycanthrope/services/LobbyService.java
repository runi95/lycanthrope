package lycanthrope.services;

import lycanthrope.models.roles.Hunter;
import lycanthrope.models.roles.Tanner;
import lycanthrope.models.roles.Werewolf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import lycanthrope.models.*;
import lycanthrope.repositories.LobbyRepository;

import java.time.LocalDateTime;
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

    @Autowired
    private PlayerRoleService playerRoleService;

    @Autowired
    private GameResultService gameResultService;

    @Autowired
    private GameResultPlayerService gameResultPlayerService;

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
                }, new Date(System.currentTimeMillis() + 7000)); // Used to be 30000
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
        }, new Date(System.currentTimeMillis() + 5000)); // Used to be 20000
    }

    private void scheduleNightAction(int lobbyId) {
        Optional<Lobby> optionalLobby = findLobbyById(lobbyId);
        if (!optionalLobby.isPresent()) {
            return;
        }

        optionalLobby.get().setState(4);
        save(optionalLobby.get());

        // User doppelganger = null;
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
            if (robber.getPlayer().getNightActionTarget() != null && robber.getPlayer().getNightActionTarget().length() > 0) {
                try {
                    int robberTarget = Integer.parseInt(robber.getPlayer().getNightActionTarget().substring(1));

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
        }

        if (troublemaker != null) {
            Integer troublemakerTargetOne = null;
            Integer troublemakerTargetTwo = null;
            try {
                if (troublemaker.getPlayer().getNightActionTarget() != null && troublemaker.getPlayer().getNightActionTarget().length() > 0) {
                    troublemakerTargetOne = Integer.parseInt(troublemaker.getPlayer().getNightActionTarget().substring(1));
                }
                if (troublemaker.getPlayer().getNightActionTargetTwo() != null && troublemaker.getPlayer().getNightActionTargetTwo().length() > 0) {
                    troublemakerTargetTwo = Integer.parseInt(troublemaker.getPlayer().getNightActionTargetTwo().substring(1));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (troublemakerTargetOne != null && troublemakerTargetTwo != null) {
                Optional<User> optionalTroublemakerTargetUserOne = userService.findById(troublemakerTargetOne);
                Optional<User> optionalTroublemakerTargetUserTwo = userService.findById(troublemakerTargetTwo);

                if (optionalTroublemakerTargetUserOne.isPresent() && optionalTroublemakerTargetUserTwo.isPresent()) {
                    int targetOneRole = optionalTroublemakerTargetUserOne.get().getPlayer().getRoleId();
                    int targetTwoRole = optionalTroublemakerTargetUserTwo.get().getPlayer().getRoleId();

                    optionalTroublemakerTargetUserOne.get().getPlayer().setRoleId(targetTwoRole);
                    optionalTroublemakerTargetUserTwo.get().getPlayer().setRoleId(targetOneRole);

                    userService.save(optionalTroublemakerTargetUserOne.get());
                    userService.save(optionalTroublemakerTargetUserTwo.get());
                }
            }
        }

        if (drunk != null) {
            try {
                if (drunk.getPlayer().getNightActionTarget() != null) {
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

        taskScheduler.schedule(
                () -> scheduleVoteAction(lobbyId),
                new Date(System.currentTimeMillis() + 3000) // Used to be 100000
        );
    }

    private void scheduleVoteAction(int lobbyId) {
        WebSocketResponseMessage<String> webSocketResponseMessage = new WebSocketResponseMessage<>();
        webSocketResponseMessage.setStatus(200);
        webSocketResponseMessage.setAction("requestVoteAction");
        webSocketResponseMessage.setContent("");

        simpTemplate.convertAndSend("/endpoint/broadcast", webSocketResponseMessage);

        taskScheduler.schedule(
                () -> scheduleGameEnd(lobbyId),
                new Date(System.currentTimeMillis() + 10000)
        );
    }

    private void scheduleGameEnd(int lobbyId) {
        // The people with the most votes gets lynched at the end of the game
        // If nobody has more than 1 votes then nobody dies (this means that town wins if none of the players were werewolves, otherwise werewolves win)
        // If there's a tie then all the players with the tied votes (above 1) will die and if a werewolf dies then town wins

        Optional<Lobby> optionalLobby = lobbyRepository.findById(lobbyId);
        if (!optionalLobby.isPresent()) {
            System.out.println("Could not find any lobbies with the given id " + lobbyId);

            return;
        }

        ArrayList<String> lynchedPlayerVotes = new ArrayList<>();
        int highestVoteCount = 0;

        Map<String, Integer> playerMap = new HashMap<>();

        boolean isAWerewolfInPlay = false;
        for(User user : optionalLobby.get().getUsers()) {
            if (playerRoleService.getRole(user.getPlayer().getRoleId()) instanceof Werewolf) {
                isAWerewolfInPlay = true;
            }

            String vote = user.getPlayer().getVote();
            if (vote != null && vote.length() > 0) {
                if (playerMap.containsKey(vote)) {
                    int newVoteCount = playerMap.get(vote) + 1;
                    if (newVoteCount > 1) {
                        if (newVoteCount > highestVoteCount) {
                            lynchedPlayerVotes.clear();
                            lynchedPlayerVotes.add(vote);
                            highestVoteCount = newVoteCount;
                        } else if (newVoteCount == highestVoteCount) {
                            lynchedPlayerVotes.add(vote);
                        }
                    }

                    playerMap.put(vote, newVoteCount);
                } else {
                    playerMap.put(vote, 1);
                }
            }
        }

        boolean isAWerewolfDead = false;
        boolean isTannerDead = false;
        boolean isHunterDead = false;
        String deadHunterId = null;
        for(String s : lynchedPlayerVotes) {
            if (s.length() > 1) {
                Integer userId = null;

                try {
                    userId = Integer.parseInt(s.substring(1));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (userId != null && userId > 0) {
                    Optional<User> optionalUser = userService.findById(userId);
                    if (optionalUser.isPresent()) {
                        PlayerRole playerRole = playerRoleService.getRole(optionalUser.get().getPlayer().getRoleId());
                        if (playerRole instanceof Werewolf) {
                            isAWerewolfDead = true;
                        } else if (playerRole instanceof Tanner) {
                            isTannerDead = true;
                        } else if (playerRole instanceof Hunter) {
                            deadHunterId = Long.toString(optionalUser.get().getPlayer().getUser().getId());
                            isHunterDead = true;
                        }
                    }
                }
            }
        }

        if (isHunterDead) {
            WebSocketResponseMessage<String> webSocketResponseMessage = new WebSocketResponseMessage<>();
            webSocketResponseMessage.setStatus(200);
            webSocketResponseMessage.setAction("requestHunterKill");
            webSocketResponseMessage.setContent("");

            simpTemplate.convertAndSendToUser(deadHunterId,"/endpoint/private", webSocketResponseMessage);

            final boolean isAWerewolfDeadFinal = isAWerewolfDead;
            final boolean isAWerewolfInPlayFinal = isAWerewolfInPlay;
            final boolean isTannerDeadFinal = isTannerDead;

            taskScheduler.schedule(
                    () -> endGame(isAWerewolfDeadFinal, isAWerewolfInPlayFinal, isTannerDeadFinal, lynchedPlayerVotes, optionalLobby.get().getUsers()),
                    new Date(System.currentTimeMillis() + 10000)
            );
        } else {
            endGame(isAWerewolfDead, isAWerewolfInPlay, isTannerDead, lynchedPlayerVotes, optionalLobby.get().getUsers());
        }
    }

    private void endGame(boolean isAWerewolfDead, boolean isAWerewolfInPlay, boolean isTannerDead, ArrayList<String> lynchedPlayerVotes, List<User> users) {
        Team winnerTeam = null;

        if (isAWerewolfDead) {
            winnerTeam = Team.Village;
        } else if (!isAWerewolfInPlay && lynchedPlayerVotes.size() == 0) {
            winnerTeam = Team.Village;
        } else if (isAWerewolfInPlay && !isTannerDead) {
            winnerTeam = Team.Werewolf;
        } else if (isTannerDead) {
            winnerTeam = Team.Tanner;
        }

        GameResult gameResult = new GameResult();
        gameResult.setGameEndTime(LocalDateTime.now());

        for(User user : users) {
            PlayerRole playerRole = playerRoleService.getRole(user.getPlayer().getRoleId());
            boolean isDead = false;
            String playerVoteId = "u" + user.getId();

            for (String s : lynchedPlayerVotes) {
                if (s.equals(playerVoteId)) {
                    isDead = true;
                }
            }

            GameResultPlayer gameResultPlayer = new GameResultPlayer();
            gameResultPlayer.setNickname(user.getNickname());
            gameResultPlayer.setRole(playerRole.getName());
            gameResultPlayer.setWinner(playerRole.getTeam() == winnerTeam || (playerRole instanceof Tanner && isDead));
            gameResultPlayer.setDead(isDead);

            gameResult.addGameResultPlayer(gameResultPlayer);
        }

        gameResultService.save(gameResult);

        WebSocketResponseMessage<String> webSocketResponseMessage = new WebSocketResponseMessage<>();
        webSocketResponseMessage.setStatus(200);
        webSocketResponseMessage.setAction("requestGameEndAction");
        webSocketResponseMessage.setContent(Long.toString(gameResult.getId()));

        simpTemplate.convertAndSend("/endpoint/broadcast", webSocketResponseMessage);
    }
}