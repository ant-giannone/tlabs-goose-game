package org.tlabs.game.goose.component;

import org.tlabs.game.goose.component.ui.SimpleViewerType;

public interface AppInfoComponent {

    public String getInfoApp();

    SimpleViewerType getDefaultViewerType();

    String getDefaultLogsViewer();
}
