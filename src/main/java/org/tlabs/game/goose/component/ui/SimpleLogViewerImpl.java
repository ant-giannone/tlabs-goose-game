package org.tlabs.game.goose.component.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tlabs.game.goose.component.AppInfoComponent;
import org.tlabs.game.goose.component.AppInfoComponentImpl;

public class SimpleLogViewerImpl implements SimpleViewerComponent {

    private Logger LOGGER;
    private AppInfoComponent appInfoComponent;


    public SimpleLogViewerImpl() {

        appInfoComponent = new AppInfoComponentImpl();
        LOGGER = LoggerFactory.getLogger(appInfoComponent.getDefaultLogsViewer());
    }

    @Override
    public void view(String message) {

        LOGGER.info(message);
    }

    @Override
    public void view(String message, Object... parameters) {

        LOGGER.info(message, parameters);
    }
}
