package org.tlabs.game.goose.component;

import org.tlabs.game.goose.component.ui.SimpleViewerType;

import java.util.List;

public interface AppInfoComponent {

    String getInfoApp();

    SimpleViewerType getDefaultViewerType();

    String getDefaultLogsViewer();

    int getVictoryBoardCellNumber();

    int getBridgeCell();

    int getJumpCellFromBridge();

    List<Integer> getGooseCells();
}
