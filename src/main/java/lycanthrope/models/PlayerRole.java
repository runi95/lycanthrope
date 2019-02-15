package lycanthrope.models;

public interface PlayerRole {

    Team getTeam();

    String getName();

    NightAction[] getNightActions(Lobby lobby);
}