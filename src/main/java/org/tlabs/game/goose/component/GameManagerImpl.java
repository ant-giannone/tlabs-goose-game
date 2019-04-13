package org.tlabs.game.goose.component;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tlabs.game.goose.component.ui.SimpleUiViewerFactoryImpl;
import org.tlabs.game.goose.component.ui.SimpleViewerComponent;
import org.tlabs.game.goose.domain.Board;
import org.tlabs.game.goose.domain.Player;
import org.tlabs.game.goose.domain.PlayerStatus;
import org.tlabs.game.goose.exception.ApplicationException;
import org.tlabs.game.goose.exception.UnknownPlayerException;
import org.tlabs.game.goose.exception.UnknownRequestFormatException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GameManagerImpl implements GameManager {

    private static Logger LOGGER = LoggerFactory.getLogger(GameManagerImpl.class);

    private static Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

    private Board board;
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
        appInfoComponent = new ProxyAppInfoComponent();

        simpleViewerComponent = SimpleUiViewerFactoryImpl.getInstance().create(appInfoComponent.getDefaultViewerType());
    }

    private static class SingletonHelper {
        private static final GameManagerImpl INSTANCE = new GameManagerImpl();
    }

    public static GameManagerImpl getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public void gameTerminatedWithError() {
        simpleViewerComponent.view(messagesResourceBundle.getString("application.message.panic_error"));
    }

    @Override
    public void initGame() throws ApplicationException {

        LOGGER.info("START :: init-game");

        simpleViewerComponent.view(appInfoComponent.getInfoApp());

        if(board == null) {
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

        if (CollectionUtils.isEmpty(players)) {

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

                if(keyTerm.equals(KeyTerms.ADD)) {
                    addPlayer(request);
                } else if(keyTerm.equals(KeyTerms.MOVE)) {
                    movePlayer(request);

                    finished = board.isCompleted();
                }
            } catch (UnknownRequestFormatException e) {

                LOGGER.error("PROCESSING :: play-game - An error occurred: {}", e.getMessage());
                simpleViewerComponent.view(messagesResourceBundle.getString("application.message.not_understand_request"));
            } catch (UnknownPlayerException e) {

                LOGGER.error("PROCESSING :: play-game - An error occurred: {}", e.getMessage());
                simpleViewerComponent.view(messagesResourceBundle.getString("application.message.unknown_player_to_move"));
            }
        }
    }

    @Override
    public void addPlayer(String request) throws UnknownRequestFormatException {

        LOGGER.info("START :: add-player");

        String newPlayerName = requestAnalyzer.doYouWantAddPlayer(request);
        Player player = new Player.Builder(newPlayerName).build();

        if(!isDuplicatePlayer(player)) {

            this.players.add(player);
            this.board.addPlayer(player);

            showPlayers();

            LOGGER.info("PROCESSING :: add-player - new player added with name: {}", player.getName());
        }else {

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
    public void movePlayer(String request) throws UnknownRequestFormatException, UnknownPlayerException {

        LOGGER.info("START :: add-player");

        Set<Player> players = board.getPlayers();
        Pair<Player, PlayerStatus> statusPair = requestAnalyzer.howManyStepFor(players, request);

        PlayerStatus playerStatus = board.getPlayerStatus(statusPair.getKey());

        String message = messagesResourceBundle.getString("application.message.player_move");

        int nextCell = playerStatus.getCurrentCell() + statusPair.getValue().getLastSteps();
        int lastBoadCell = board.getFinalCell();

        if(playerStatus.isStart()) {

            simpleViewerComponent.view(MessageFormat.format(message,
                    statusPair.getKey().getName(),
                    statusPair.getValue().getLastDiceRoll(),
                    statusPair.getKey().getName(),
                    "Start",
                    nextCell));

            playerStatus.setCurrentCell(nextCell);
        }else if(lastBoadCell==nextCell){

            simpleViewerComponent.view(MessageFormat.format(
                    messagesResourceBundle.getString("application.message.player_move_adn_win"),
                    statusPair.getKey().getName(),
                    statusPair.getValue().getLastDiceRoll(),
                    statusPair.getKey().getName(),
                    playerStatus.getCurrentCell(),
                    nextCell,
                    statusPair.getKey().getName()));
            board.setCompleted(true);
        }else if(nextCell>lastBoadCell){

            int bounces = nextCell - lastBoadCell;
            int returnTo = lastBoadCell - bounces;

            simpleViewerComponent.view(MessageFormat.format(
                    messagesResourceBundle.getString("application.message.player_move_and_bounces"),
                    statusPair.getKey().getName(),
                    statusPair.getValue().getLastDiceRoll(),
                    statusPair.getKey().getName(),
                    playerStatus.getCurrentCell(),
                    lastBoadCell,
                    statusPair.getKey().getName(),
                    statusPair.getKey().getName(),
                    returnTo));

            playerStatus.setCurrentCell(returnTo);
        }else {

            simpleViewerComponent.view(MessageFormat.format(message,
                    statusPair.getKey().getName(),
                    statusPair.getValue().getLastDiceRoll(),
                    statusPair.getKey().getName(),
                    playerStatus.getCurrentCell(),
                    nextCell));

            playerStatus.setCurrentCell(nextCell);
        }

        playerStatus.setLastSteps(statusPair.getValue().getLastSteps());
        playerStatus.setLastDiceRoll(statusPair.getValue().getLastDiceRoll());

        LOGGER.info("END :: add-player");
    }

    private boolean isDuplicatePlayer(final Player newPlayer) {

        Optional<Player> optionalPlayer = players.stream().findAny().filter(
                player -> newPlayer.getName().equals(player.getName()));

        if (optionalPlayer.isPresent()) {

            return true;
        }

        return false;
    }
}
