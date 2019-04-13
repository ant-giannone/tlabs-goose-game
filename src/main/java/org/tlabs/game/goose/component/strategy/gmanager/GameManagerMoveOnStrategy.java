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

public class GameManagerMoveOnStrategy implements GameManagerStrategy {

    private static Logger LOGGER = LoggerFactory.getLogger(GameManagerMoveOnStrategy.class);

    private AppInfoComponent appInfoComponent;
    private ResourceBundle messagesResourceBundle;


    public GameManagerMoveOnStrategy(
            AppInfoComponent appInfoComponent,
            ResourceBundle messagesResourceBundle) {

        this.appInfoComponent = appInfoComponent;
        this.messagesResourceBundle = messagesResourceBundle;
    }

    @Override
    public String execute(final Board board,
                          final Pair<Player, PlayerStatus> calculatedPlayerStats) {

        final PlayerStatus playerStatus = board.getPlayerStatus(calculatedPlayerStats.getKey());

        int nextCell = playerStatus.getCurrentCell() + calculatedPlayerStats.getValue().getLastSteps();
        String message = messagesResourceBundle.getString("application.message.player_move");

       String  messageToView = MessageFormat.format(message,
                calculatedPlayerStats.getKey().getName(),
                calculatedPlayerStats.getValue().getLastDiceRoll(),
                calculatedPlayerStats.getKey().getName(),
                playerStatus.getCurrentCell(),
                nextCell);

        playerStatus.setCurrentCell(nextCell);
        playerStatus.setLastSteps(calculatedPlayerStats.getValue().getLastSteps());
        playerStatus.setLastDiceRoll(calculatedPlayerStats.getValue().getLastDiceRoll());

        return messageToView;
    }
}
