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

public class GameManagerGooseStrategyTest {

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

        Locale locale = new Locale.Builder().setLanguage(Locale.ENGLISH.getLanguage()).build();
        messagesResourceBundle = ResourceBundle.getBundle("i18n.messages", locale);
        appInfoComponent = new ProxyAppInfoComponent();
        gameManagerStrategy = new GameManagerGooseStrategy(appInfoComponent, messagesResourceBundle);

        board = new Board(appInfoComponent.getVictoryBoardCellNumber());

        alice = new Player.Builder("alice").build();
        board.addPlayer(alice);

        bob = new Player.Builder("bob").build();
        board.addPlayer(bob);

        alicePs = board.getPlayerStatus(alice);
        alicePs.setLastSteps(4);
        alicePs.setLastDiceRoll("2,2");
        alicePs.setCurrentCell(23);

        bobPs = board.getPlayerStatus(bob);
        bobPs.setLastSteps(5);
        bobPs.setLastDiceRoll("2,3");
        bobPs.setCurrentCell(10);


        PlayerStatus aliceCalculatedStatus = new PlayerStatus();
        aliceCalculatedStatus.setLastSteps(4);
        aliceCalculatedStatus.setLastDiceRoll("2,2");
        aliceCalculatedStatus.setCurrentCell(23);

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
    public void execute_single_jump() {

        PlayerStatus playerStatus = board.getPlayerStatus(alice);

        String periodOne = MessageFormat.format(
                messagesResourceBundle.getString("application.message.player_move_and_goose_1"),
                aliceCalculatedStats.getKey().getName(),
                aliceCalculatedStats.getValue().getLastDiceRoll(),
                aliceCalculatedStats.getKey().getName(),
                (playerStatus.isStart()) ? "Start" : playerStatus.getCurrentCell(),
                playerStatus.getCurrentCell() + playerStatus.getLastSteps());

        String periodTwo = MessageFormat.format(
                messagesResourceBundle.getString("application.message.player_move_and_goose_2"),
                aliceCalculatedStats.getKey().getName(),
                playerStatus.getCurrentCell() + (playerStatus.getLastSteps()*2));

        //let's not forget the end-of-sentence point
        String messageToView = periodOne + periodTwo + ".";

        String message = gameManagerStrategy.execute(board, aliceCalculatedStats);

        Assert.assertTrue(message.equals(messageToView));
    }

    @Test
    public void execute_multi_jump() {

        PlayerStatus playerStatus = board.getPlayerStatus(bob);

        String theGoose = messagesResourceBundle.getString("application.message.player_move_and_goose_3");

        String periodOne = MessageFormat.format(
                messagesResourceBundle.getString("application.message.player_move_and_goose_1"),
                bobCalculatedStats.getKey().getName(),
                bobCalculatedStats.getValue().getLastDiceRoll(),
                bobCalculatedStats.getKey().getName(),
                (playerStatus.isStart()) ? "Start" : playerStatus.getCurrentCell(),
                playerStatus.getCurrentCell() + playerStatus.getLastSteps());

        String periodTwo = MessageFormat.format(
                messagesResourceBundle.getString("application.message.player_move_and_goose_2"),
                bobCalculatedStats.getKey().getName(),
                playerStatus.getCurrentCell() + (playerStatus.getLastSteps()*2));

        String periodThree = MessageFormat.format(
                messagesResourceBundle.getString("application.message.player_move_and_goose_2"),
                bobCalculatedStats.getKey().getName(),
                playerStatus.getCurrentCell() + (playerStatus.getLastSteps()*3));

        //let's not forget the end-of-sentence point
        String messageToView = periodOne + periodTwo + theGoose + periodThree + ".";

        String message = gameManagerStrategy.execute(board, bobCalculatedStats);

        Assert.assertTrue(message.equals(messageToView));
    }
}