package org.tlabs.game.goose.domain;

import org.tlabs.game.goose.exception.PlayerValidationRuntimeException;

public class Player {

    private String name;

    private Player() {
        name = null;
    }

    public String getName() {
        return name;
    }

    public static class Builder {

        private String name;

        public Builder(final String name) {

            this.name = name;
        }

        public Player build() {

            this.validate();

            Player player = new Player();
            player.name = this.name;

            return player;
        }

        private void validate() {

            if(this.name == null || this.name.isEmpty()) {
                throw new PlayerValidationRuntimeException("Player's name must not be null or empty");
            }
        }
    }
}
