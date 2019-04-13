package org.tlabs.game.goose.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class Board {

    private HashMap<Player, PlayerStatus> players;
    private final int finalCell;
    private boolean completed;

    public Board(final int finalCell) {

        this.finalCell = finalCell;
        players = new HashMap<>();
        completed = false;
    }

    public void addPlayer(final Player player) {

        players.put(player, new PlayerStatus());
    }

    public Set<Player> getPlayers() {

        return Collections.unmodifiableSet(players.keySet());
    }

    public PlayerStatus getPlayerStatus(Player player) {

        return players.get(player);
    }

    public int getFinalCell() {
        return finalCell;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
