package org.tlabs.game.goose.component;

import org.apache.commons.lang3.tuple.Pair;
import org.tlabs.game.goose.domain.Player;
import org.tlabs.game.goose.domain.PlayerStatus;
import org.tlabs.game.goose.exception.UnknownPlayerException;
import org.tlabs.game.goose.exception.UnknownRequestFormatException;
import org.tlabs.game.goose.exception.UnknownStrategyException;

import java.util.Set;

public interface RequestAnalyzer {

    KeyTerms getKeyTerm(String request) throws UnknownRequestFormatException;

    public String doYouWantAddPlayer(String request) throws UnknownRequestFormatException;

    public boolean doYouDigitQuit(String request) throws UnknownRequestFormatException;

    Pair<Player, PlayerStatus> howManyStepFor(final Set<Player> players, String request)
            throws UnknownRequestFormatException, UnknownPlayerException, UnknownStrategyException;
}
