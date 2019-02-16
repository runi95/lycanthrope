package lycanthrope.services;

import lycanthrope.configs.WebSocketConfig;
import lycanthrope.models.*;
import lycanthrope.models.roles.Hunter;
import lycanthrope.models.roles.Insomniac;
import lycanthrope.models.roles.Tanner;
import lycanthrope.models.roles.Werewolf;
import lycanthrope.repositories.LobbyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Semaphore;

@Service
public class LobbyService {

    public static final int GAME_ROLE_REVEAL_TIME = 30;
    public static final int GAME_DISCUSSION_TIME = 600;
    public static final int GAME_VOTE_TIME = 30;
    public static final int GAME_NIGHT_ACTION_TIME = 30;
    public static final int GAME_HUNTER_KILL_TIME = 20;

    private final Semaphore joinLobbySemaphore = new Semaphore(1);
    private final Semaphore createLobbySemaphore = new Semaphore(1);

    private Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private SimpMessagingTemplate simpTemplate;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerRoleService playerRoleService;

    @Autowired
    private GameResultService gameResultService;

    @Autowired
    private FreemarkerService freemarkerService;

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

                        if (playerRoleService.getRole(player.getRoleId()) instanceof Insomniac) {
                            player.setRealInsomniac(true);
                        }

                        playerService.save(player);
                    }

                    int one = iterator.next().ordinal();
                    int two = iterator.next().ordinal();
                    int three = iterator.next().ordinal();

                    lobby.setNeutralOne(one);
                    lobby.setNeutralTwo(two);
                    lobby.setNeutralThree(three);

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
                WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
                webSocketResponseMessage.setAction("requestGameRoleReveal");
                webSocketResponseMessage.setContent("");
                webSocketResponseMessage.setStatus(200);

                simpTemplate.convertAndSend("/endpoint/broadcast", webSocketResponseMessage);

                int lobbyId = lobby.getId();
                startCountdown(GAME_ROLE_REVEAL_TIME, () -> scheduleRoleReveal(lobbyId));
            } else {
                WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
                webSocketResponseMessage.setAction("joinLobby");
                webSocketResponseMessage.setContent(new Pair<>(user, optionalLobby.get()));
                webSocketResponseMessage.setStatus(200);

                simpTemplate.convertAndSend("/endpoint/broadcast", webSocketResponseMessage);
            }

            return Optional.of(lobby);
        } else {
            return Optional.empty();
        }
    }

    public void startCountdownHunter(int seconds, String deadHunterId, Runnable runnable) {
        new Thread(() -> {
            int i = seconds - 1;
            while (i >= 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
                webSocketResponseMessage.setAction("setTime");
                webSocketResponseMessage.setContent((100.00 * i) / (double)seconds);
                webSocketResponseMessage.setStatus(200);

                simpTemplate.convertAndSendToUser(deadHunterId, "/endpoint/private", webSocketResponseMessage);

                i--;
            }

            taskScheduler.schedule(runnable, new Date(System.currentTimeMillis() + 2000));
        }).start();
    }

    public void startCountdown(int seconds, Runnable runnable) {
        new Thread(() -> {
            int i = seconds - 1;
            while (i >= 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                WebSocketResponseMessage webSocketResponseMessage = new WebSocketResponseMessage();
                webSocketResponseMessage.setAction("setTime");
                webSocketResponseMessage.setContent((100.00 * i) / (double)seconds);
                webSocketResponseMessage.setStatus(200);

                simpTemplate.convertAndSend("/endpoint/broadcast", webSocketResponseMessage);

                i--;
            }

            taskScheduler.schedule(runnable, new Date(System.currentTimeMillis() + 2000));
        }).start();
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
        startCountdown(GAME_NIGHT_ACTION_TIME, () -> scheduleNightAction(lobbyId));
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
            if (robber.getPlayer().getNightActionTarget() != null && robber.getPlayer().getNightActionTarget().length() > 1) {
                try {
                    int robberTargetId = Integer.parseInt(robber.getPlayer().getNightActionTarget().substring(1));

                    Optional<User> optionalRobberTargetUser = userService.findById(robberTargetId);
                    if (optionalRobberTargetUser.isPresent()) {
                        User robberTarget = optionalRobberTargetUser.get();
                        int robberNewRole = robberTarget.getPlayer().getRoleId();
                        int robberOldRole = robber.getPlayer().getRoleId();

                        robber.getPlayer().setRoleId(robberNewRole);
                        playerService.save(robber.getPlayer());

                        robberTarget.getPlayer().setRoleId(robberOldRole);
                        playerService.save(robberTarget.getPlayer());
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
                if (troublemaker.getPlayer().getNightActionTarget() != null && troublemaker.getPlayer().getNightActionTarget().length() > 1) {
                    troublemakerTargetOne = Integer.parseInt(troublemaker.getPlayer().getNightActionTarget().substring(1));
                }
                if (troublemaker.getPlayer().getNightActionTargetTwo() != null && troublemaker.getPlayer().getNightActionTargetTwo().length() > 1) {
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
                if (drunk.getPlayer().getNightActionTarget() != null && drunk.getPlayer().getNightActionTarget().length() > 1) {
                    int drunkTarget = Integer.parseInt(drunk.getPlayer().getNightActionTarget().substring(1));

                    // TODO: Find out how you want to improve this (low priority)
                    switch (drunkTarget) {
                        case 1:
                            int drunkNewRole = optionalLobby.get().getNeutralOne();
                            int drunkOldRole = drunk.getPlayer().getRoleId();

                            drunk.getPlayer().setRoleId(drunkNewRole);
                            playerService.save(drunk.getPlayer());

                            optionalLobby = lobbyRepository.findById(lobbyId); // TODO: VERY UGLY CODE, FIX IT!!!
                            optionalLobby.get().setNeutralOne(drunkOldRole);
                            lobbyRepository.save(optionalLobby.get());
                            break;
                        case 2:
                            int drunkNewRoleTwo = optionalLobby.get().getNeutralTwo();
                            int drunkOldRoleTwo = drunk.getPlayer().getRoleId();

                            drunk.getPlayer().setRoleId(drunkNewRoleTwo);
                            playerService.save(drunk.getPlayer());

                            optionalLobby = lobbyRepository.findById(lobbyId); // TODO: VERY UGLY CODE, FIX IT!!!
                            optionalLobby.get().setNeutralTwo(drunkOldRoleTwo);
                            lobbyRepository.save(optionalLobby.get());
                            break;
                        case 3:
                            int drunkNewRoleThree = optionalLobby.get().getNeutralThree();
                            int drunkOldRoleThree = drunk.getPlayer().getRoleId();

                            drunk.getPlayer().setRoleId(drunkNewRoleThree);
                            playerService.save(drunk.getPlayer());

                            optionalLobby = lobbyRepository.findById(lobbyId); // TODO: VERY UGLY CODE, FIX IT!!!
                            optionalLobby.get().setNeutralThree(drunkOldRoleThree);
                            lobbyRepository.save(optionalLobby.get());
                            break;
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        WebSocketResponseMessage<String> webSocketResponseMessage = new WebSocketResponseMessage<>();
        webSocketResponseMessage.setStatus(200);
        webSocketResponseMessage.setAction("requestGame");
        webSocketResponseMessage.setContent("");

        simpTemplate.convertAndSend("/endpoint/broadcast", webSocketResponseMessage);
        startCountdown(GAME_DISCUSSION_TIME, () -> scheduleVoteAction(lobbyId));
    }

    private void scheduleVoteAction(int lobbyId) {
        Optional<Lobby> optionalLobby = lobbyRepository.findById(lobbyId);
        if (optionalLobby.isPresent()) {
            optionalLobby.get().setState(5);
            lobbyRepository.save(optionalLobby.get());
        }

        WebSocketResponseMessage<String> webSocketResponseMessage = new WebSocketResponseMessage<>();
        webSocketResponseMessage.setStatus(200);
        webSocketResponseMessage.setAction("requestVoteAction");
        webSocketResponseMessage.setContent("");

        simpTemplate.convertAndSend("/endpoint/broadcast", webSocketResponseMessage);

        startCountdown(GAME_VOTE_TIME, () -> scheduleGameEnd(lobbyId));
    }

    private void scheduleGameEnd(int lobbyId) {
        // The people with the most votes gets lynched at the end of the game
        // If nobody has more than 1 votes then nobody dies (this means that town wins if none of the players were werewolves, otherwise werewolves win)
        // If there's a tie then all the players with the tied votes (above 1) will die and if a werewolf dies then town wins

        Optional<Lobby> optionalLobby = lobbyRepository.findById(lobbyId);
        if (!optionalLobby.isPresent()) {
            logger.warn("Could not find any lobbies with the given id " + lobbyId);

            return;
        }

        ArrayList<String> lynchedPlayerVotes = new ArrayList<>();
        int highestVoteCount = 0;

        Map<String, Integer> playerMap = new HashMap<>();

        boolean isAWerewolfInPlay = false;
        for (User user : optionalLobby.get().getUsers()) {
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
        HashMap<Long, User> deadUsers = new HashMap<>();
        for (String s : lynchedPlayerVotes) {
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
                        deadUsers.put(optionalUser.get().getId(), optionalUser.get());
                        if (playerRole instanceof Werewolf) {
                            isAWerewolfDead = true;
                        } else if (playerRole instanceof Tanner) {
                            isTannerDead = true;
                        } else if (playerRole instanceof Hunter) {
                            deadHunterId = optionalUser.get().getNickname();
                            isHunterDead = true;
                        }
                    }
                }
            }
        }

        if (isHunterDead) {
            try {
                ArrayList<User> users = new ArrayList<>();
                optionalLobby.get().getUsers().forEach(u -> {
                    if (!playerMap.containsKey(u.getId()))
                        users.add(u);
                });

                Map map = new HashMap();
                map.put("users", users);

                WebSocketResponseMessage<String> webSocketResponseMessage = freemarkerService.parseTemplate("hunterKill", map);
                simpTemplate.convertAndSendToUser(deadHunterId, "/endpoint/private", webSocketResponseMessage);
            } catch (Exception e) {
                logger.error("Failed to parse hunterKill.ftlh!");
            }

            final boolean isAWerewolfDeadFinal = isAWerewolfDead;
            final boolean isAWerewolfInPlayFinal = isAWerewolfInPlay;
            final boolean isTannerDeadFinal = isTannerDead;

            startCountdownHunter(GAME_HUNTER_KILL_TIME, deadHunterId, () -> endGame(isAWerewolfDeadFinal, isAWerewolfInPlayFinal, isTannerDeadFinal, lynchedPlayerVotes, lobbyId));
        } else {
            endGame(isAWerewolfDead, isAWerewolfInPlay, isTannerDead, lynchedPlayerVotes, lobbyId);
        }
    }

    private void endGame(boolean isAWerewolfDead, boolean isAWerewolfInPlay, boolean isTannerDead, ArrayList<String> lynchedPlayerVotes, int lobbyId) {
        Optional<Lobby> optionalLobby = lobbyRepository.findById(lobbyId);
        if (!optionalLobby.isPresent()) {
            logger.error("FATAL: We lost our lobbyId in endGame()");

            return;
        }

        Long hunterKillId = null;
        if (optionalLobby.get().getHunterKill() != null && optionalLobby.get().getHunterKill().length() > 1) {
            try {
                hunterKillId = Long.parseLong(optionalLobby.get().getHunterKill().substring(1));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (hunterKillId != null) {
                Optional<User> optionalUser = userService.findById(hunterKillId);
                if (optionalUser.isPresent()) {
                    PlayerRole playerRole = playerRoleService.getRole(optionalUser.get().getPlayer().getRoleId());
                    if (playerRole instanceof Werewolf) {
                        isAWerewolfDead = true;
                    } else if (playerRole instanceof Tanner) {
                        isTannerDead = true;
                    }
                }
            }
        }

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

        for (User user : optionalLobby.get().getUsers()) {
            PlayerRole playerRole = playerRoleService.getRole(user.getPlayer().getRoleId());
            boolean isDead = false;
            String playerVoteId = "u" + user.getId();

            for (String s : lynchedPlayerVotes) {
                if (s.equals(playerVoteId)) {
                    isDead = true;
                }
            }

            if (hunterKillId != null && user.getId() == hunterKillId) {
                isDead = true;
            }

            GameResultPlayer gameResultPlayer = new GameResultPlayer();
            gameResultPlayer.setNickname(user.getNickname());
            gameResultPlayer.setRole(playerRole.getName());
            gameResultPlayer.setWinner(playerRole.getTeam() == winnerTeam || (playerRole instanceof Tanner && isDead));
            gameResultPlayer.setDead(isDead);

            gameResult.addGameResultPlayer(gameResultPlayer);
        }

        gameResultService.save(gameResult);

        // The game is over, we don't need this lobby anymore
        lobbyRepository.delete(optionalLobby.get());

        WebSocketResponseMessage<String> webSocketResponseMessage = new WebSocketResponseMessage<>();
        webSocketResponseMessage.setStatus(200);
        webSocketResponseMessage.setAction("requestGameEndAction");
        webSocketResponseMessage.setContent(Long.toString(gameResult.getId()));

        simpTemplate.convertAndSend("/endpoint/broadcast", webSocketResponseMessage);
    }
}