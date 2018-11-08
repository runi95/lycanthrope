package lycanthrope.models;

public interface PlayerRole {

    boolean isAbleToPerformOnDeathAction();

    int getOrderRank();

    Team getTeam();
}
