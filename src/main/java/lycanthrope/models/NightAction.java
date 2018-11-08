package lycanthrope.models;

public interface NightAction {
    boolean isAbleToTargetNeutrals();

    boolean isAbleToTargetOtherPlayers();

    boolean isAbleToTargetSelf();
}
