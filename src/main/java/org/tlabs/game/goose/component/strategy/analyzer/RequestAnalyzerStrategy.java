package org.tlabs.game.goose.component.strategy.analyzer;

import org.tlabs.game.goose.domain.PlayerStatus;
import org.tlabs.game.goose.exception.UnknownRequestFormatException;

public interface RequestAnalyzerStrategy {

    PlayerStatus execute(String[] terms) throws UnknownRequestFormatException;
}
