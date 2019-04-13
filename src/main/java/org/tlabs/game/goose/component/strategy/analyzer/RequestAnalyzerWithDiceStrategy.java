package org.tlabs.game.goose.component.strategy.analyzer;

import org.tlabs.game.goose.domain.Dice;
import org.tlabs.game.goose.domain.PlayerStatus;
import org.tlabs.game.goose.exception.UnknownRequestFormatException;

public class RequestAnalyzerWithDiceStrategy implements RequestAnalyzerStrategy {

    @Override
    public PlayerStatus execute(String[] terms) throws UnknownRequestFormatException {

        if(!"move".equals(terms[0])) {
            throw new UnknownRequestFormatException("Unable to understand request grammar: terms counter doesn't match");
        }

        Dice one = new Dice();
        Dice two = new Dice();

        int steps = one.getResult() + two.getResult();
        String diceRoll = String.format("%s, %s", one.getResult(), two.getResult());

        PlayerStatus playerStatus = new PlayerStatus();
        playerStatus.setLastDiceRoll(diceRoll);
        playerStatus.setLastSteps(steps);

        return playerStatus;
    }
}
