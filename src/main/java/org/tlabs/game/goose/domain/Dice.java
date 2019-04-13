package org.tlabs.game.goose.domain;

public class Dice {

    int result;

    public Dice() {
        result = (int) (Math.random() * 6 + 1);
    }

    public int getResult() {
        return result;
    }
}
