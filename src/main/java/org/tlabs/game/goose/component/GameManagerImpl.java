package org.tlabs.game.goose.component;


import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tlabs.game.goose.component.ui.SimpleUiViewerFactory;
import org.tlabs.game.goose.component.ui.SimpleUiViewerFactoryImpl;
import org.tlabs.game.goose.component.ui.SimpleViewerComponent;
import org.tlabs.game.goose.domain.Player;
import org.tlabs.game.goose.exception.DuplicatePlayerException;
import org.tlabs.game.goose.exception.UnknownRequestFormatException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GameManagerImpl implements GameManager {

    private static Logger LOGGER = LoggerFactory.getLogger(GameManagerImpl.class);

    private static Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

    private List<Player> players;
    private ResourceBundle messagesResourceBundle;
    private RequestAnalyzer requestAnalyzer;
    private AppInfoComponent appInfoComponent;
    private SimpleViewerComponent simpleViewerComponent;

    private GameManagerImpl() {

        players = new ArrayList<>();

        Locale locale = Locale.getDefault();
        messagesResourceBundle = ResourceBundle.getBundle("i18n.messages", locale);

        requestAnalyzer = new ProxyRequestAnalyzer();
        appInfoComponent = new AppInfoComponentImpl();

        SimpleUiViewerFactory<SimpleViewerComponent> simpleUiViewerFactory = SimpleUiViewerFactoryImpl.getInstance();

        simpleViewerComponent = simpleUiViewerFactory.create(appInfoComponent.getDefaultViewerType());
    }

    private static class SingletonHelper {
        private static final GameManagerImpl INSTANCE = new GameManagerImpl();
    }

    public static GameManagerImpl getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public void initGame() {

        LOGGER.info("START :: init-game");

        simpleViewerComponent.view(appInfoComponent.getInfoApp());

        if (CollectionUtils.isEmpty(players)) {

            LOGGER.debug("PROCESSING :: init-game - players list is empty: create new player");

            simpleViewerComponent.view(messagesResourceBundle.getString("application.message.no_participant"));

            addPlayer();
            showPlayers();
        }

        LOGGER.info("END :: init-game");
    }

    @Override
    public void addPlayer() {

        LOGGER.info("START :: add-player");

        String request = null;
        boolean isRequestAccomplished = false;

        while (!isRequestAccomplished) {

            try {

                if (scanner.ioException() != null) {

                    simpleViewerComponent.view(messagesResourceBundle.getString("application.message.panic_error"));

                    LOGGER.error("PROCESSING :: add-player - an error occurred on I/O stream, game interrupted: {}",
                            scanner.ioException().getMessage());

                    LOGGER.info("END :: add-player");

                    throw new RuntimeException("Game interrupted, restart the game...");
                }

                request = scanner.nextLine();

                boolean doYouDigitQuit = requestAnalyzer.doYouDigitQuit(request);

                if (doYouDigitQuit) {

                    LOGGER.debug("PROCESSING :: add-player - processing interrupted: request is 'quit'");
                    break;
                }

                String newPlayerName = requestAnalyzer.doYouWantAddPlayer(request);
                Player player = new Player.Builder(newPlayerName).build();

                isRequestAccomplished = addCurrentPlayer(player);
            } catch (UnknownRequestFormatException e) {

                simpleViewerComponent.view(messagesResourceBundle.getString("application.message.not_understand_request"));

                LOGGER.warn("PROCESSING :: add-player - an error occurred: {}", e.getMessage());
            }
        }

        LOGGER.info("END :: add-player");
    }

    @Override
    public void showPlayers() {

        String players = this.players.stream().map(
                player -> player.getName()).collect(Collectors.joining(","));

        String message = messagesResourceBundle.getString("application.message.players.list");

        simpleViewerComponent.view(MessageFormat.format(message, players));
    }

    private boolean isDuplicatePlayer(final Player newPlayer) {

        Optional<Player> optionalPlayer = players.stream().findAny().filter(
                player -> newPlayer.getName().equals(player.getName()));

        if (optionalPlayer.isPresent()) {

            return true;
        }

        return false;
    }

    private boolean addCurrentPlayer(final Player player) {

        if(isDuplicatePlayer(player)) {

            simpleViewerComponent.view(
                    MessageFormat.format(
                            messagesResourceBundle.getString("application.message.duplicate_player"),
                            player.getName())
            );

            return false;
        }

        this.players.add(player);

        LOGGER.info("PROCESSING :: add-player - new player added with name: {}", player.getName());

        return true;
    }
}
