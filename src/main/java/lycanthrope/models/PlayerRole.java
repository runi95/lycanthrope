package lycanthrope.models;

public interface PlayerRole {

    boolean isAbleToPerformOnDeathAction();

    Team getTeam();

    String getName();

    NightAction[] getNightActions(Lobby lobby);
}