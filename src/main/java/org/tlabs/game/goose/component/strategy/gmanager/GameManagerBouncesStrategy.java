package org.tlabs.game.goose.component.strategy.gmanager;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tlabs.game.goose.component.AppInfoComponent;
import org.tlabs.game.goose.domain.Board;
import org.tlabs.game.goose.domain.Player;
import org.tlabs.game.goose.domain.PlayerStatus;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class GameManagerBouncesStrategy implements GameManagerStrategy {

    private static Logger LOGGER = LoggerFactory.getLogger(GameManagerBouncesStrategy.class);

    private AppInfoComponent appInfoComponent;
    private ResourceBundle messagesResourceBundle;


    public GameManagerBouncesStrategy(
            AppInfoComponent appInfoComponent,
            ResourceBundle messagesResourceBundle) {

        this.appInfoComponent = appInfoComponent;
        this.messagesResourceBundle = messagesResourceBundle;
    }

    @Override
    public String execute(final Board board,
                          final Pair<Player, PlayerStatus> calculatedPlayerStats) {

        final PlayerStatus playerStatus = board.getPlayerStatus(calculatedPlayerStats.getKey());

        int lastBoadCell = board.getFinalCell();
        int nextCell = playerStatus.getCurrentCell() + calculatedPlayerStats.getValue().getLastSteps();

        int bounces = nextCell - lastBoadCell;
        int returnTo = lastBoadCell - bounces;

        String messageToView = MessageFormat.format(
                messagesResourceBundle.getString("application.message.player_move_and_bounces"),
                calculatedPlayerStats.getKey().getName(),
                calculatedPlayerStats.getValue().getLastDiceRoll(),
                calculatedPlayerStats.getKey().getName(),
                playerStatus.getCurrentCell(),
                lastBoadCell,
                calculatedPlayerStats.getKey().getName(),
                calculatedPlayerStats.getKey().getName(),
                returnTo);

        playerStatus.setCurrentCell(returnTo);
        playerStatus.setLastSteps(calculatedPlayerStats.getValue().getLastSteps());
        playerStatus.setLastDiceRoll(calculatedPlayerStats.getValue().getLastDiceRoll());

        return messageToView;
    }
}
