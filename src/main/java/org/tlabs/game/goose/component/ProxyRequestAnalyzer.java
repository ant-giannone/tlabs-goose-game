package org.tlabs.game.goose.component;

import org.apache.commons.lang3.tuple.Pair;
import org.tlabs.game.goose.domain.Player;
import org.tlabs.game.goose.exception.UnknownPlayerException;
import org.tlabs.game.goose.exception.UnknownRequestFormatException;

import java.util.List;

public class ProxyRequestAnalyzer implements RequestAnalyzer {

    private RequestAnalyzer requestAnalyzer;

    private void checkInstance() {

        if(requestAnalyzer==null) {
            requestAnalyzer = new RequestAnalyzerImpl();
        }
    }

    @Override
    public KeyTerms getKeyTerm(String request) throws UnknownRequestFormatException {

        checkInstance();
        return requestAnalyzer.getKeyTerm(request);
    }

    @Override
    public String doYouWantAddPlayer(String request) throws UnknownRequestFormatException {

        checkInstance();
        return requestAnalyzer.doYouWantAddPlayer(request);
    }

    @Override
    public boolean doYouDigitQuit(String request) throws UnknownRequestFormatException {

        checkInstance();
        return requestAnalyzer.doYouDigitQuit(request);
    }

    @Override
    public Pair<Player, Integer> howManyStepFor(List<Player> players, String request)
            throws UnknownRequestFormatException, UnknownPlayerException {

        checkInstance();
        return requestAnalyzer.howManyStepFor(players, request);
    }
}
