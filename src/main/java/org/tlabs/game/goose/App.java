package org.tlabs.game.goose;

import org.tlabs.game.goose.component.GameManager;
import org.tlabs.game.goose.component.GameManagerImpl;
import org.tlabs.game.goose.exception.ApplicationException;

/**
 * Hello world!
 *
 */
public class App {

    public static void main( String[] args ) {

        GameManager gameManager = GameManagerImpl.getInstance();

        try {

            gameManager.initGame();
            gameManager.playGame();
        } catch (ApplicationException e) {

            gameManager.gameTerminatedWithError();
        }
    }
}
