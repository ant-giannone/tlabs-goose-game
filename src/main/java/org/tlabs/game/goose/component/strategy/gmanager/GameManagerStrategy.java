package org.tlabs.game.goose.component.strategy.gmanager;

import org.apache.commons.lang3.tuple.Pair;
import org.tlabs.game.goose.domain.Board;
import org.tlabs.game.goose.domain.Player;
import org.tlabs.game.goose.domain.PlayerStatus;

public interface GameManagerStrategy {

    String execute(Board board,
                   Pair<Player, PlayerStatus> calculatedPlayerStats);
}
