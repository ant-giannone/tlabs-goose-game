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

public class GameManagerGooseStrategy implements GameManagerStrategy {

    private static Logger LOGGER = LoggerFactory.getLogger(GameManagerGooseStrategy.class);

    private AppInfoComponent appInfoComponent;
    private ResourceBundle messagesResourceBundle;


    public GameManagerGooseStrategy(
            AppInfoComponent appInfoComponent,
            ResourceBundle messagesResourceBundle) {

        this.appInfoComponent = appInfoComponent;
        this.messagesResourceBundle = messagesResourceBundle;
    }

    @Override
    public String execute(final Board board,
                          final Pair<Player, PlayerStatus> calculatedPlayerStats) {

        PlayerStatus playerStatus = board.getPlayerStatus(calculatedPlayerStats.getKey());
        List<Integer> gooseCells = appInfoComponent.getGooseCells();
        int nextCell = playerStatus.getCurrentCell() + calculatedPlayerStats.getValue().getLastSteps();

        int cellForGoose = nextCell;
        boolean isMultiJump = false;
        String messageForGoose = "";

        do {

            if(isMultiJump) {

                LOGGER.debug("PROCESSING :: move-player - is multi-jump for goose: from {} to {}",
                        cellForGoose, playerStatus.getLastSteps());

                cellForGoose = playerStatus.getCurrentCell();

                String moveAgainMessage = MessageFormat.format(
                        messagesResourceBundle.getString("application.message.player_move_and_goose_2"),
                        calculatedPlayerStats.getKey().getName(),
                        cellForGoose + playerStatus.getLastSteps());

                messageForGoose += moveAgainMessage;

                //Update Player status stored on the board: update only the current cell for the player
                playerStatus.setCurrentCell(cellForGoose + playerStatus.getLastSteps());
            }else {

                int nextCellForGoose = cellForGoose + calculatedPlayerStats.getValue().getLastSteps();

                //Pippo rolls 1, 1. Pippo moves from 3 to 5, The Goose. Pippo moves again and goes to 7
                String periodOne = MessageFormat.format(
                        messagesResourceBundle.getString("application.message.player_move_and_goose_1"),
                        calculatedPlayerStats.getKey().getName(),
                        calculatedPlayerStats.getValue().getLastDiceRoll(),
                        calculatedPlayerStats.getKey().getName(),
                        (playerStatus.isStart())?"Start":playerStatus.getCurrentCell(),
                        cellForGoose);

                String periodTwo = MessageFormat.format(
                        messagesResourceBundle.getString("application.message.player_move_and_goose_2"),
                        calculatedPlayerStats.getKey().getName(),
                        nextCellForGoose);

                messageForGoose = periodOne + periodTwo;


                LOGGER.debug("PROCESSING :: move-player - is first/single jump for goose: from {} to {}",
                        cellForGoose, playerStatus.getLastSteps());

                //Update Player status stored on the board
                playerStatus.setCurrentCell(nextCellForGoose);
                playerStatus.setLastDiceRoll(calculatedPlayerStats.getValue().getLastDiceRoll());
                playerStatus.setLastSteps(calculatedPlayerStats.getValue().getLastSteps());

            }

            isMultiJump = gooseCells.contains(playerStatus.getCurrentCell());

            String theGooseExclamation =
                    (isMultiJump)?
                            messagesResourceBundle.getString("application.message.player_move_and_goose_3")
                            :".";

            messageForGoose += theGooseExclamation;
        }while(isMultiJump);

        return messageForGoose;
    }
}
