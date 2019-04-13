package org.tlabs.game.goose.component;

import org.tlabs.game.goose.component.ui.SimpleViewerType;

public interface AppInfoComponent {

    String getInfoApp();

    SimpleViewerType getDefaultViewerType();

    String getDefaultLogsViewer();

    int getVictoryBoardCellNumber();

    int getBridgeCell();

    int getJumpCellFromBridge();
}
