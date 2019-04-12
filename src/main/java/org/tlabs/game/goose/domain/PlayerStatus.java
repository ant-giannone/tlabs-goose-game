package org.tlabs.game.goose.domain;

public class PlayerStatus {

    private int currentCell;//zero is start
    private int lastSteps;
    private String lastDiceRoll;

    public PlayerStatus() {
        currentCell = 0;
    }

    public boolean isStart() {
        return currentCell == 0;
    }

    public int getCurrentCell() {
        return currentCell;
    }

    public void setCurrentCell(int currentCell) {
        this.currentCell = currentCell;
    }

    public String getLastDiceRoll() {
        return lastDiceRoll;
    }

    public void setLastDiceRoll(String lastDiceRoll) {
        this.lastDiceRoll = lastDiceRoll;
    }

    public int getLastSteps() {
        return lastSteps;
    }

    public void setLastSteps(int lastSteps) {
        this.lastSteps = lastSteps;
    }
}
