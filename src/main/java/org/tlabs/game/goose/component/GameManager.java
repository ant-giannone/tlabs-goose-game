package org.tlabs.game.goose.component;

import org.tlabs.game.goose.exception.ApplicationException;
import org.tlabs.game.goose.exception.UnknownRequestFormatException;

public interface GameManager {

    void gameTerminatedWithError();

    public void initGame() throws ApplicationException;

    void playGame() throws ApplicationException;

    public void addPlayer(String request) throws UnknownRequestFormatException;
    public void showPlayers();

    void movePlayer(String request) throws UnknownRequestFormatException;
}
