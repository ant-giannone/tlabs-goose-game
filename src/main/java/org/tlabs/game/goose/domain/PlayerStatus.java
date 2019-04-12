package org.tlabs.game.goose.domain;

public class PlayerStatus {

    private int currentCell;//zero is start

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
}
