package org.tlabs.game.goose.component.strategy;

import org.tlabs.game.goose.domain.PlayerStatus;
import org.tlabs.game.goose.exception.UnknownRequestFormatException;

public interface MovePlayerStrategy {

    PlayerStatus execute(String[] terms) throws UnknownRequestFormatException;
}
