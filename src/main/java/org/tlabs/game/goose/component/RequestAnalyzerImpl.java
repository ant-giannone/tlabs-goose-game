package org.tlabs.game.goose.component;

import org.apache.commons.lang3.StringUtils;
import org.tlabs.game.goose.exception.UnknownRequestFormatException;

public class RequestAnalyzerImpl implements RequestAnalyzer {

    private String preAnalyzeRequest(String request) throws UnknownRequestFormatException {

        if(StringUtils.isEmpty(request)) {
            throw new UnknownRequestFormatException("Unable to understand request grammar: data is empty");
        }

        final String normalizedRequest = StringUtils.normalizeSpace(request);

        if(StringUtils.isEmpty(normalizedRequest)) {
            throw new UnknownRequestFormatException("Unable to understand request grammar: normalized data is empty");
        }

        return normalizedRequest;
    }

    @Override
    public String doYouWantAddPlayer(String request) throws UnknownRequestFormatException {

        String preAnalyzedRequest = preAnalyzeRequest(request);

        String[] terms = preAnalyzedRequest.split(" ");

        if(terms==null || terms.length!=3) {
            throw new UnknownRequestFormatException("Unable to understand request grammar: terms counter doesn't match");
        }

        if(!("add".equals(terms[0]) && "player".equals(terms[1]))) {

            throw new UnknownRequestFormatException("Unable to understand request grammar: grammar not correspond");
        }

        if(StringUtils.isEmpty(terms[2])) {

            throw new UnknownRequestFormatException("Unable to understand request grammar: player's name missed");
        }

        return terms[2];
    }

    @Override
    public boolean doYouDigitQuit(String request) throws UnknownRequestFormatException {

        String preAnalyzedRequest = preAnalyzeRequest(request);

        return "quit".equals(preAnalyzedRequest);
    }
}
