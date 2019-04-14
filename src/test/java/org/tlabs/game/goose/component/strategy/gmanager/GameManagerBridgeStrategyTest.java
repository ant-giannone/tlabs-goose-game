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

public class GameManagerBridgeStrategyTest {

    private Board board;
    private Player alice;
    private Player bob;
    private PlayerStatus alicePs;
    private PlayerStatus bobPs;
    private Pair<Player, PlayerStatus> aliceCalculatedStats;
    private Pair<Player, PlayerStatus> bobCalculatedStats;

    private ResourceBundle messagesResourceBundle;
    private AppInfoComponent appInfoComponent;
    private GameManagerStrategy gameManagerStrategy;

    @Before
    public void setUp() throws Exception {

        Locale locale = Locale.getDefault();
        messagesResourceBundle = ResourceBundle.getBundle("i18n.messages", locale);
        appInfoComponent = new ProxyAppInfoComponent();
        gameManagerStrategy = new GameManagerBridgeStrategy(appInfoComponent, messagesResourceBundle);

        board = new Board(appInfoComponent.getVictoryBoardCellNumber());

        alice = new Player.Builder("alice").build();
        board.addPlayer(alice);

        bob = new Player.Builder("bob").build();
        board.addPlayer(bob);

        alicePs = board.getPlayerStatus(alice);
        alicePs.setLastSteps(2);
        alicePs.setLastDiceRoll("1,1");
        alicePs.setCurrentCell(3);

        bobPs = board.getPlayerStatus(bob);
        bobPs.setLastSteps(8);
        bobPs.setLastDiceRoll("5,3");
        bobPs.setCurrentCell(42);


        PlayerStatus aliceCalculatedStatus = new PlayerStatus();
        aliceCalculatedStatus.setLastSteps(3);
        aliceCalculatedStatus.setLastDiceRoll("1,2");
        aliceCalculatedStatus.setCurrentCell(6);

        PlayerStatus bobCalculatedStatus = new PlayerStatus();
        bobCalculatedStatus.setLastSteps(5);
        bobCalculatedStatus.setLastDiceRoll("2,3");
        bobCalculatedStatus.setCurrentCell(5);

        aliceCalculatedStats = Pair.of(alice, aliceCalculatedStatus);
        bobCalculatedStats = Pair.of(bob, bobCalculatedStatus);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void execute() {

        final PlayerStatus playerStatus = board.getPlayerStatus(alice);
        String startPoint = messagesResourceBundle.getString("application.message.space.start");
        int jumpCellFromBridge = appInfoComponent.getJumpCellFromBridge();

        String messageToView = MessageFormat.format(
                messagesResourceBundle.getString("application.message.player_move_and_bridge"),
                aliceCalculatedStats.getKey().getName(),
                aliceCalculatedStats.getValue().getLastDiceRoll(),
                aliceCalculatedStats.getKey().getName(),
                (playerStatus.isStart()) ? startPoint : playerStatus.getCurrentCell(),
                aliceCalculatedStats.getKey().getName(),
                jumpCellFromBridge);

        String message = gameManagerStrategy.execute(board, aliceCalculatedStats);

        Assert.assertTrue(message.equals(messageToView));
    }
}