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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.*;

public class GameManagerBouncesStrategyTest {

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

        Locale locale = new Locale.Builder().setLanguage(Locale.ENGLISH.getLanguage()).build();
        messagesResourceBundle = ResourceBundle.getBundle("i18n.messages", locale);
        appInfoComponent = new ProxyAppInfoComponent();
        gameManagerStrategy = new GameManagerBouncesStrategy(appInfoComponent, messagesResourceBundle);

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
        bobPs.setCurrentCell(60);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void execute() {

        Pair<Player, PlayerStatus> bobStats = Pair.of(bob, bobPs);

        String messageToView = MessageFormat.format(
                messagesResourceBundle.getString("application.message.player_move_and_bounces"),
                bobStats.getKey().getName(),
                bobStats.getValue().getLastDiceRoll(),
                bobStats.getKey().getName(),
                bobStats.getValue().getCurrentCell(),
                board.getFinalCell(),
                bobStats.getKey().getName(),
                bobStats.getKey().getName(),
                58);

        String message = gameManagerStrategy.execute(board, bobStats);

        Assert.assertTrue(message.equals(messageToView));
    }
}