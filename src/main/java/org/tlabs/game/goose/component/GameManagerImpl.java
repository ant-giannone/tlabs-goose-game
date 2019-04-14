package org.tlabs.game.goose.component;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tlabs.game.goose.component.strategy.gmanager.*;
import org.tlabs.game.goose.component.ui.SimpleUiViewerFactoryImpl;
import org.tlabs.game.goose.component.ui.SimpleViewerComponent;
import org.tlabs.game.goose.domain.Board;
import org.tlabs.game.goose.domain.Player;
import org.tlabs.game.goose.domain.PlayerStatus;
import org.tlabs.game.goose.exception.ApplicationException;
import org.tlabs.game.goose.exception.UnknownPlayerException;
import org.tlabs.game.goose.exception.UnknownRequestFormatException;
import org.tlabs.game.goose.exception.UnknownStrategyException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GameManagerImpl implements GameManager {

    private static Logger LOGGER = LoggerFactory.getLogger(GameManagerImpl.class);

    private static Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

    private Board board;
    private ResourceBundle messagesResourceBundle;
    private RequestAnalyzer requestAnalyzer;
    private AppInfoComponent appInfoComponent;
    private SimpleViewerComponent simpleViewerComponent;
    private HashMap<String, GameManagerStrategy> gameManagerStrategyRegistry;

    private GameManagerImpl() {

        gameManagerStrategyRegistry = new HashMap<>();

        Locale locale = new Locale.Builder().setLanguage(Locale.ENGLISH.getLanguage()).build();
        messagesResourceBundle = ResourceBundle.getBundle("i18n.messages", locale);

        appInfoComponent = new ProxyAppInfoComponent();
        requestAnalyzer = new ProxyRequestAnalyzer(appInfoComponent);

        simpleViewerComponent = SimpleUiViewerFactoryImpl.getInstance().create(appInfoComponent.getDefaultViewerType());
    }

    private static class SingletonHelper {
        private static final GameManagerImpl INSTANCE = new GameManagerImpl();
    }

    public static GameManagerImpl getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private GameManagerStrategy getGameManagerStrategy(Class<? extends GameManagerStrategy> strategyClass) {

        if (!gameManagerStrategyRegistry.containsKey(strategyClass.getName())) {

            if (strategyClass.getName().equals(GameManagerBouncesStrategy.class.getName())) {
                gameManagerStrategyRegistry.put(strategyClass.getName(),
                        new GameManagerBouncesStrategy(appInfoComponent, messagesResourceBundle));
            } else if (strategyClass.getName().equals(GameManagerBridgeStrategy.class.getName())) {
                gameManagerStrategyRegistry.put(strategyClass.getName(),
                        new GameManagerBridgeStrategy(appInfoComponent, messagesResourceBundle));
            } else if (strategyClass.getName().equals(GameManagerGooseStrategy.class.getName())) {
                gameManagerStrategyRegistry.put(strategyClass.getName(),
                        new GameManagerGooseStrategy(appInfoComponent, messagesResourceBundle));
            } else if (strategyClass.getName().equals(GameManagerMoveOnStrategy.class.getName())) {
                gameManagerStrategyRegistry.put(strategyClass.getName(),
                        new GameManagerMoveOnStrategy(appInfoComponent, messagesResourceBundle));
            } else if (strategyClass.getName().equals(GameManagerStartPointStrategy.class.getName())) {
                gameManagerStrategyRegistry.put(strategyClass.getName(),
                        new GameManagerStartPointStrategy(appInfoComponent, messagesResourceBundle));
            } else if (strategyClass.getName().equals(GameManagerWinStrategy.class.getName())) {
                gameManagerStrategyRegistry.put(strategyClass.getName(),
                        new GameManagerWinStrategy(appInfoComponent, messagesResourceBundle));
            }
        }

        return gameManagerStrategyRegistry.get(strategyClass.getName());
    }

    @Override
    public void gameTerminatedWithError() {
        simpleViewerComponent.view(messagesResourceBundle.getString("application.message.panic_error"));
    }

    @Override
    public void initGame() throws ApplicationException {

        LOGGER.info("START :: init-game");

        simpleViewerComponent.view(appInfoComponent.getInfoApp());

        if (board == null) {
            board = new Board(appInfoComponent.getVictoryBoardCellNumber());
        }

        boolean finished = false;
        String request = null;

        if (scanner.ioException() != null) {

            simpleViewerComponent.view(messagesResourceBundle.getString("application.message.panic_error"));

            LOGGER.error("PROCESSING :: init-game - an error occurred on I/O stream, game interrupted: {}",
                    scanner.ioException().getMessage());

            LOGGER.info("END :: init-game ");

            throw new ApplicationException("Game interrupted, restart the game...", scanner.ioException());
        }

        if (CollectionUtils.isEmpty(board.getPlayers())) {

            LOGGER.debug("PROCESSING :: init-game - players list is empty: create new player");

            simpleViewerComponent.view(messagesResourceBundle.getString("application.message.no_participant"));

            while (!finished) {

                request = scanner.nextLine();

                try {

                    addPlayer(request);
                    finished = true;
                } catch (UnknownRequestFormatException e) {

                    LOGGER.error("PROCESSING :: init-game - An error occurred: {}", e.getMessage());
                    simpleViewerComponent.view(messagesResourceBundle.getString("application.message.not_understand_request"));
                }
            }
        }

        LOGGER.info("END :: init-game");
    }

    @Override
    public void playGame() throws ApplicationException {

        LOGGER.info("START :: play-game");

        boolean finished = false;
        String request = null;

        while (!finished) {

            if (scanner.ioException() != null) {

                simpleViewerComponent.view(messagesResourceBundle.getString("application.message.panic_error"));

                LOGGER.error("PROCESSING :: play-game - an error occurred on I/O stream, game interrupted: {}",
                        scanner.ioException().getMessage());

                LOGGER.info("END :: play-game ");

                throw new ApplicationException("Game interrupted, restart the game...", scanner.ioException());
            }

            request = scanner.nextLine();

            try {

                KeyTerms keyTerm = requestAnalyzer.getKeyTerm(request);

                if (keyTerm.equals(KeyTerms.ADD)) {
                    addPlayer(request);
                } else if (keyTerm.equals(KeyTerms.MOVE)) {
                    movePlayer(request);

                    finished = board.isCompleted();
                }
            } catch (UnknownRequestFormatException e) {

                LOGGER.error("PROCESSING :: play-game - An error occurred: {}", e.getMessage());
                simpleViewerComponent.view(messagesResourceBundle.getString("application.message.not_understand_request"));
            } catch (UnknownPlayerException e) {

                LOGGER.error("PROCESSING :: play-game - An error occurred: {}", e.getMessage());
                simpleViewerComponent.view(messagesResourceBundle.getString("application.message.unknown_player_to_move"));
            } catch (UnknownStrategyException e) {

                simpleViewerComponent.view(messagesResourceBundle.getString("application.message.panic_error"));

                LOGGER.error("PROCESSING :: play-game - an error occurred on Strategy selection, game interrupted: {}", e);

                LOGGER.info("END :: play-game ");

                throw new ApplicationException("Game interrupted, restart the game...", e);
            }
        }
    }

    @Override
    public void addPlayer(String request) throws UnknownRequestFormatException {

        LOGGER.info("START :: add-player");

        String newPlayerName = requestAnalyzer.doYouWantAddPlayer(request);
        Player player = new Player.Builder(newPlayerName).build();

        if (!isDuplicatePlayer(player)) {

            this.board.addPlayer(player);

            showPlayers();

            LOGGER.info("PROCESSING :: add-player - new player added with name: {}", player.getName());
        } else {

            simpleViewerComponent.view(
                    MessageFormat.format(
                            messagesResourceBundle.getString("application.message.duplicate_player"),
                            player.getName())
            );
        }

        LOGGER.info("END :: add-player");
    }

    @Override
    public void showPlayers() {

        String players = this.board.getPlayers().stream().map(
                player -> player.getName()).collect(Collectors.joining(", "));

        String message = messagesResourceBundle.getString("application.message.players.list");

        simpleViewerComponent.view(MessageFormat.format(message, players));
    }

    @Override
    public void movePlayer(String request) throws UnknownRequestFormatException,
            UnknownPlayerException, UnknownStrategyException {

        LOGGER.info("START :: move-player");

        /**
         * The player object returned is exactly the same object on the board as reference.
         * No new instance is created, so I can perform comparison and search processing by reference object
         * */
        Set<Player> players = board.getPlayers();
        Pair<Player, PlayerStatus> statusPair = requestAnalyzer.howManyStepFor(players, request);
        PlayerStatus playerStatus = board.getPlayerStatus(statusPair.getKey());

        int currentPlayerLastCell = playerStatus.getCurrentCell();
        int bridgeCell = appInfoComponent.getBridgeCell();

        List<Integer> gooseCells = appInfoComponent.getGooseCells();

        int nextCell = playerStatus.getCurrentCell() + statusPair.getValue().getLastSteps();
        int lastBoardCell = board.getFinalCell();
        String messageToView = "";

        if (gooseCells.contains(nextCell)) {

            messageToView = getGameManagerStrategy(GameManagerGooseStrategy.class).execute(board, statusPair);
        } else if (nextCell == bridgeCell) {

            messageToView = getGameManagerStrategy(GameManagerBridgeStrategy.class).execute(board, statusPair);
        } else if (playerStatus.isStart()) {

            messageToView = getGameManagerStrategy(GameManagerStartPointStrategy.class).execute(board, statusPair);
        } else if (lastBoardCell == nextCell) {

            messageToView = getGameManagerStrategy(GameManagerWinStrategy.class).execute(board, statusPair);
        } else if (nextCell > lastBoardCell) {

            messageToView = getGameManagerStrategy(GameManagerBouncesStrategy.class).execute(board, statusPair);
        } else {

            messageToView = getGameManagerStrategy(GameManagerMoveOnStrategy.class).execute(board, statusPair);
        }

        checkCollisionAndMovePlayerTo(messageToView, currentPlayerLastCell, playerStatus, statusPair.getKey());

        LOGGER.info("END :: move-player");
    }

    private boolean isDuplicatePlayer(final Player newPlayer) {

        Optional<Player> optionalPlayer = board.getPlayers().stream().findAny().filter(
                player -> newPlayer.getName().equals(player.getName()));

        if (optionalPlayer.isPresent()) {

            return true;
        }

        return false;
    }

    private void checkCollisionAndMovePlayerTo(
            final String messageToView, int currentPlayerLastCell,
            final PlayerStatus currentPlayerStatus, final Player currentPlayer) {

        String defaultMessageToView = messageToView;

        Optional<Player> optPlayer = board.getPlayers().stream().findFirst().filter(player -> {

            if (!currentPlayer.getName().equals(player.getName())) {

                PlayerStatus playerStatus = board.getPlayerStatus(player);

                if (playerStatus.getCurrentCell() == currentPlayerStatus.getCurrentCell()) {
                    return true;
                }
            }

            return false;
        });

        if (optPlayer.isPresent()) {

            Player player = optPlayer.get();

            defaultMessageToView = MessageFormat.format(
                    messagesResourceBundle.getString("application.message.player_move_and_shared_location"),
                    currentPlayer.getName(),
                    currentPlayerStatus.getLastDiceRoll(),
                    currentPlayer.getName(),
                    currentPlayerLastCell,
                    currentPlayerStatus.getCurrentCell(),
                    currentPlayerStatus.getCurrentCell(),
                    player.getName(),
                    currentPlayerLastCell == 0 ? "Start" : currentPlayerLastCell);
        }

        simpleViewerComponent.view(defaultMessageToView);
    }
}
