package org.tlabs.game.goose.component;

import org.apache.commons.lang3.tuple.Pair;
import org.tlabs.game.goose.domain.Player;
import org.tlabs.game.goose.exception.UnknownPlayerException;
import org.tlabs.game.goose.exception.UnknownRequestFormatException;

import java.util.List;

public interface RequestAnalyzer {

    KeyTerms getKeyTerm(String request)  throws UnknownRequestFormatException;

    public String doYouWantAddPlayer(String request) throws UnknownRequestFormatException;
    public boolean doYouDigitQuit(String request) throws UnknownRequestFormatException;

    Pair<Player, Integer> howManyStepFor(List<Player> players, String request)
            throws UnknownRequestFormatException, UnknownPlayerException;
}
