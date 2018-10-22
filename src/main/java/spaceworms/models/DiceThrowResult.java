package spaceworms.models;

public class DiceThrowResult {

    private boolean gameEnded;
    private int winningPlayerId;
    private int diceThrowResult;
    private int diceThrowLandingSquare;
    private int lastPlayerId;
    private int nextPlayerId;

    public boolean isGameEnded() { return gameEnded; }
    public void setGameEnded(boolean gameEnded) { this.gameEnded = gameEnded; }

    public int getWinningPlayerId() { return winningPlayerId; }
    public void setWinningPlayerId(int winningPlayerId) { this.winningPlayerId = winningPlayerId; }

    public int getDiceThrowResult() { return diceThrowResult; }
    public void setDiceThrowResult(int diceThrowResult) { this.diceThrowResult = diceThrowResult; }

    public int getDiceThrowLandingSquare() { return diceThrowLandingSquare; }
    public void setDiceThrowLandingSquare(int diceThrowLandingSquare) { this.diceThrowLandingSquare = diceThrowLandingSquare; }

    public int getLastPlayerId() { return lastPlayerId; }
    public void setLastPlayerId(int lastPlayerId) { this.lastPlayerId = lastPlayerId; }

    public int getNextPlayerId() { return nextPlayerId; }
    public void setNextPlayerId(int nextPlayerId) { this.nextPlayerId = nextPlayerId; }
}
