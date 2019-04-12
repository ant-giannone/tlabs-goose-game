package org.tlabs.game.goose.component.ui;

public enum SimpleViewerType {

    CONSOLE("console"),
    LOGS("logs");

    private final String value;

    SimpleViewerType(final String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return this.value;
    }
}
