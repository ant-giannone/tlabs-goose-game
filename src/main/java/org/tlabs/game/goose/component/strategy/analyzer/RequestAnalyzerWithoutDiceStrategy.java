package org.tlabs.game.goose.component.strategy.analyzer;

import org.apache.commons.lang3.math.NumberUtils;
import org.tlabs.game.goose.domain.PlayerStatus;
import org.tlabs.game.goose.exception.UnknownRequestFormatException;

public class RequestAnalyzerWithoutDiceStrategy implements RequestAnalyzerStrategy {

    @Override
    public PlayerStatus execute(String[] terms) throws UnknownRequestFormatException {

        if (!"move".equals(terms[0])) {
            throw new UnknownRequestFormatException("Unable to understand request grammar: terms counter doesn't match");
        }

        String[] diceRoll = terms[2].split(",");

        if (diceRoll == null || diceRoll.length != 2 ||
                !(NumberUtils.isDigits(diceRoll[0]) && NumberUtils.isDigits(diceRoll[1]))) {
            throw new UnknownRequestFormatException("Unable to understand request grammar: terms counter doesn't match");
        }

        int steps = Integer.parseInt(diceRoll[0]) + Integer.parseInt(diceRoll[1]);

        PlayerStatus playerStatus = new PlayerStatus();
        playerStatus.setLastDiceRoll(terms[2]);
        playerStatus.setLastSteps(steps);

        return playerStatus;
    }
}
