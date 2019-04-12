package org.tlabs.game.goose.domain;

import java.util.HashMap;
import java.util.Set;

public class Board {

    private HashMap<Player, PlayerStatus> players;
    private final int finalCell;

    public Board(final int finalCell) {

        this.finalCell = finalCell;
        players = new HashMap<>();
    }

    public void addPlayer(final Player player) {

        players.put(player, new PlayerStatus());
    }

    public Set<Player> getPlayers() {

        return players.keySet();
    }
}
