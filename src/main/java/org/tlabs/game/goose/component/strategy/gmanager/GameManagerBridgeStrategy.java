package org.tlabs.game.goose.component.strategy.gmanager;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tlabs.game.goose.component.AppInfoComponent;
import org.tlabs.game.goose.domain.Board;
import org.tlabs.game.goose.domain.Player;
import org.tlabs.game.goose.domain.PlayerStatus;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

public class GameManagerBridgeStrategy implements GameManagerStrategy {

    private static Logger LOGGER = LoggerFactory.getLogger(GameManagerBridgeStrategy.class);

    private AppInfoComponent appInfoComponent;
    private ResourceBundle messagesResourceBundle;


    public GameManagerBridgeStrategy(
            AppInfoComponent appInfoComponent,
            ResourceBundle messagesResourceBundle) {

        this.appInfoComponent = appInfoComponent;
        this.messagesResourceBundle = messagesResourceBundle;
    }

    @Override
    public String execute(final Board board,
                          final Pair<Player, PlayerStatus> calculatedPlayerStats) {

        final PlayerStatus playerStatus = board.getPlayerStatus(calculatedPlayerStats.getKey());
        int jumpCellFromBridge = appInfoComponent.getJumpCellFromBridge();
        String startPoint = messagesResourceBundle.getString("application.message.space.start");

        String messageToView = MessageFormat.format(
                messagesResourceBundle.getString("application.message.player_move_and_bridge"),
                calculatedPlayerStats.getKey().getName(),
                calculatedPlayerStats.getValue().getLastDiceRoll(),
                calculatedPlayerStats.getKey().getName(),
                (playerStatus.isStart())?startPoint:playerStatus.getCurrentCell(),
                calculatedPlayerStats.getKey().getName(),
                jumpCellFromBridge);

        playerStatus.setCurrentCell(jumpCellFromBridge);
        playerStatus.setLastSteps(calculatedPlayerStats.getValue().getLastSteps());
        playerStatus.setLastDiceRoll(calculatedPlayerStats.getValue().getLastDiceRoll());

        return messageToView;
    }
}
