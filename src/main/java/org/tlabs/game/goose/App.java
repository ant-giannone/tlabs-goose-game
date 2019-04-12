package org.tlabs.game.goose;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tlabs.game.goose.component.GameManager;
import org.tlabs.game.goose.component.GameManagerImpl;

/**
 * Hello world!
 *
 */
public class App {

    private static Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) {

        GameManager gameManager = GameManagerImpl.getInstance();
        gameManager.initGame();

        gameManager.addPlayer();
        gameManager.showPlayers();
    }
}
