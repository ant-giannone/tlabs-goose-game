package org.tlabs.game.goose.component;

import org.tlabs.game.goose.component.ui.SimpleViewerType;

import java.util.List;

public class ProxyAppInfoComponent implements AppInfoComponent {

    private AppInfoComponent appInfoComponent;

    private void checkInstance() {

        if(appInfoComponent==null) {
            appInfoComponent = new AppInfoComponentImpl();
        }
    }

    @Override
    public String getInfoApp() {
        checkInstance();
        return appInfoComponent.getInfoApp();
    }

    @Override
    public SimpleViewerType getDefaultViewerType() {
        checkInstance();
        return appInfoComponent.getDefaultViewerType();
    }

    @Override
    public String getDefaultLogsViewer() {
        checkInstance();
        return appInfoComponent.getDefaultLogsViewer();
    }

    @Override
    public int getVictoryBoardCellNumber() {
        checkInstance();
        return  appInfoComponent.getVictoryBoardCellNumber();
    }

    @Override
    public int getBridgeCell() {
        checkInstance();
        return appInfoComponent.getBridgeCell();
    }

    @Override
    public int getJumpCellFromBridge() {
        checkInstance();
        return appInfoComponent.getJumpCellFromBridge();
    }

    @Override
    public List<Integer> getGooseCells() {
        checkInstance();
        return appInfoComponent.getGooseCells();
    }
}
