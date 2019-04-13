package org.tlabs.game.goose.component.ui;

public interface SimpleViewerComponent {

    public void view(String message);

    public void view(String message, Object... parameter);
}
