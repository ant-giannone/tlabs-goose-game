package org.tlabs.game.goose.component;

import org.tlabs.game.goose.exception.UnknownRequestFormatException;

public interface RequestAnalyzer {

    public String doYouWantAddPlayer(String request) throws UnknownRequestFormatException;
    public boolean doYouDigitQuit(String request) throws UnknownRequestFormatException;
}
