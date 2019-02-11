package lycanthrope.models;

public interface NightAction {
    boolean isAbleToTargetNeutrals();

    boolean isAbleToTargetOtherPlayers();

    boolean isAbleToTargetSelf();

    boolean isAbleToViewCertainRoles();

    Roles[] getViewableRoles();
}
