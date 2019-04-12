package org.tlabs.game.goose.component.ui;

public interface SimpleUiViewerFactory<T> {

    T create(SimpleViewerType viewerType);
}
