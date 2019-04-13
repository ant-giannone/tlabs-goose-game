package org.tlabs.game.goose.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tlabs.game.goose.component.ui.SimpleViewerType;

import java.io.IOException;
import java.util.Properties;

public class AppInfoComponentImpl implements AppInfoComponent {

    private static Logger LOGGER = LoggerFactory.getLogger(AppInfoComponentImpl.class);

    private Properties properties;

    public AppInfoComponentImpl() {

        try {

            properties = new Properties();
            properties.load(AppInfoComponentImpl.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {

            LOGGER.error("Unable to read configuration properties: {}", e.getMessage());

            throw new RuntimeException("Unable to read configuration properties");
        }
    }

    @Override
    public String getInfoApp() {

        return  String.format("%s -- %s",
                    properties.getProperty("application.name"),
                    properties.getProperty("application.welcome"));
    }

    @Override
    public SimpleViewerType getDefaultViewerType() {

        return  SimpleViewerType.valueOf(properties.getProperty("application.viewer.default").toUpperCase());
    }

    @Override
    public String getDefaultLogsViewer() {

        return  properties.getProperty("application.viewer.logs.appender.name");
    }

    @Override
    public int getVictoryBoardCellNumber() {

        return Integer.parseInt(properties.getProperty("application.board.cell.victory"));
    }

    @Override
    public int getBridgeCell() {

        return Integer.parseInt(properties.getProperty("application.board.element.bridge.cell"));
    }

    @Override
    public int getJumpCellFromBridge() {

        return Integer.parseInt(properties.getProperty("application.board.element.bridge.jump_to_cell"));
    }
}
