package org.tlabs.game.goose.component;

import org.tlabs.game.goose.exception.UnknownRequestFormatException;

public class ProxyRequestAnalyzer implements RequestAnalyzer {

    private RequestAnalyzer requestAnalyzer;

    private void checkInstance() {

        if(requestAnalyzer==null) {
            requestAnalyzer = new RequestAnalyzerImpl();
        }
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
}
