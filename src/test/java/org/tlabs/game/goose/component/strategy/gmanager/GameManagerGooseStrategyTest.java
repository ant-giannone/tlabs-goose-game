package org.tlabs.game.goose.component.strategy.gmanager;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tlabs.game.goose.component.AppInfoComponent;
import org.tlabs.game.goose.component.ProxyAppInfoComponent;
import org.tlabs.game.goose.domain.Board;
import org.tlabs.game.goose.domain.Player;
import org.tlabs.game.goose.domain.PlayerStatus;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.*;

public class GameManagerGooseStrategyTest {

    private Board board;
    private Player alice;
    private Player bob;
    private PlayerStatus alicePs;
    private PlayerStatus bobPs;

    private ResourceBundle messagesResourceBundle;
    private AppInfoComponent appInfoComponent;
    private GameManagerStrategy gameManagerStrategy;

    @Before
    public void setUp() throws Exception {

        Locale locale = Locale.getDefault();
        messagesResourceBundle = ResourceBundle.getBundle("i18n.messages", locale);
        appInfoComponent = new ProxyAppInfoComponent();
        gameManagerStrategy = new GameManagerMoveOnStrategy(appInfoComponent, messagesResourceBundle);

        board = new Board(appInfoComponent.getVictoryBoardCellNumber());

        alice = new Player.Builder("alice").build();
        board.addPlayer(alice);

        bob = new Player.Builder("bob").build();
        board.addPlayer(bob);

        alicePs = board.getPlayerStatus(alice);
        alicePs.setLastSteps(5);
        alicePs.setLastDiceRoll("2,3");
        alicePs.setCurrentCell(34);

        bobPs = board.getPlayerStatus(bob);
        bobPs.setLastSteps(8);
        bobPs.setLastDiceRoll("5,3");
        bobPs.setCurrentCell(42);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void execute() {

        Pair<Player, PlayerStatus> aliceStats = Pair.of(alice, alicePs);

        String mockMessage = String.format("%s rolls %s. %s moves from %s to %s",
                alice.getName(),
                alicePs.getLastDiceRoll(),
                alice.getName(),
                alicePs.getCurrentCell(),
                (alicePs.getCurrentCell() + alicePs.getLastSteps()));

        String message = gameManagerStrategy.execute(board, aliceStats);

        Assert.assertTrue(message.equals(mockMessage));
    }
}