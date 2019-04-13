package org.tlabs.game.goose.component;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tlabs.game.goose.component.ui.SimpleUiViewerFactoryImpl;
import org.tlabs.game.goose.component.ui.SimpleViewerComponent;
import org.tlabs.game.goose.domain.Board;
import org.tlabs.game.goose.domain.Dice;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
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
        int jumpCellFromBridge = appInfoComponent.getJumpCellFromBridge();

        List<Integer> gooseCells = appInfoComponent.getGooseCells();

        String message = messagesResourceBundle.getString("application.message.player_move");

        int nextCell = playerStatus.getCurrentCell() + statusPair.getValue().getLastSteps();
        int lastBoadCell = board.getFinalCell();

        if(gooseCells.contains(nextCell)) {

            int cellForGoose = nextCell;
            boolean isMultiJump = false;
            String messageForGoose = "";

            do {

                if(isMultiJump) {

                    LOGGER.debug("PROCESSING :: move-player - is multi-jump for goose: from {} to {}",
                            cellForGoose, playerStatus.getLastSteps());

                    cellForGoose = playerStatus.getCurrentCell();

                    String moveAgainMessage = MessageFormat.format(
                            messagesResourceBundle.getString("application.message.player_move_and_goose_2"),
                            statusPair.getKey().getName(),
                            cellForGoose + playerStatus.getLastSteps());

                    messageForGoose += moveAgainMessage;

                    //Update Player status stored on the board: update only the current cell for the player
                    playerStatus.setCurrentCell(cellForGoose + playerStatus.getLastSteps());
                }else {

                    int nextCellForGoose = cellForGoose + statusPair.getValue().getLastSteps();

                    //Pippo rolls 1, 1. Pippo moves from 3 to 5, The Goose. Pippo moves again and goes to 7
                    String periodOne = MessageFormat.format(
                            messagesResourceBundle.getString("application.message.player_move_and_goose_1"),
                            statusPair.getKey().getName(),
                            statusPair.getValue().getLastDiceRoll(),
                            statusPair.getKey().getName(),
                            (playerStatus.isStart())?"Start":playerStatus.getCurrentCell(),
                            cellForGoose);

                    String periodTwo = MessageFormat.format(
                            messagesResourceBundle.getString("application.message.player_move_and_goose_2"),
                            statusPair.getKey().getName(),
                            nextCellForGoose);

                    messageForGoose = periodOne + periodTwo;


                    LOGGER.debug("PROCESSING :: move-player - is first/single jump for goose: from {} to {}",
                            cellForGoose, playerStatus.getLastSteps());

                    //Update Player status stored on the board
                    playerStatus.setCurrentCell(nextCellForGoose);
                    playerStatus.setLastDiceRoll(statusPair.getValue().getLastDiceRoll());
                    playerStatus.setLastSteps(statusPair.getValue().getLastSteps());

                }

                isMultiJump = gooseCells.contains(playerStatus.getCurrentCell());

                String theGooseExclamation =
                        (isMultiJump)?
                                messagesResourceBundle.getString("application.message.player_move_and_goose_3")
                                :".";

                messageForGoose += theGooseExclamation;
            }while(isMultiJump);

            simpleViewerComponent.view(messageForGoose);
        }else if(nextCell==bridgeCell) {

            simpleViewerComponent.view(MessageFormat.format(
                    messagesResourceBundle.getString("application.message.player_move_and_bridge"),
                    statusPair.getKey().getName(),
                    statusPair.getValue().getLastDiceRoll(),
                    statusPair.getKey().getName(),
                    (playerStatus.isStart())?"Start":playerStatus.getCurrentCell(),
                    statusPair.getKey().getName(),
                    jumpCellFromBridge));

            playerStatus.setCurrentCell(jumpCellFromBridge);
        }else if(playerStatus.isStart()) {

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

        checkCollisionAndMovePlayerTo(currentPlayerLastCell, playerStatus, statusPair.getKey());

        LOGGER.info("END :: move-player");
    }

    private boolean isDuplicatePlayer(final Player newPlayer) {

        Optional<Player> optionalPlayer = players.stream().findAny().filter(
                player -> newPlayer.getName().equals(player.getName()));

        if (optionalPlayer.isPresent()) {

            return true;
        }

        return false;
    }

    private void checkCollisionAndMovePlayerTo(
            int currentPlayerLastCell, PlayerStatus currentPlayerStatus, Player currentPlayer) {

        board.getPlayers().stream().forEach(player -> {

            if(!currentPlayer.getName().equals(player.getName())) {

                PlayerStatus playerStatus = board.getPlayerStatus(player);

                if(playerStatus.getCurrentCell()==currentPlayerStatus.getCurrentCell()) {
                    playerStatus.setCurrentCell(currentPlayerLastCell);

                    //Pippo rolls 1, 1. Pippo moves from 15 to 17. On 17 there is Pluto, who returns to 15
                    simpleViewerComponent.view(MessageFormat.format(
                            messagesResourceBundle.getString("application.message.player_move_and_shared_location"),
                            currentPlayer.getName(),
                            currentPlayerStatus.getLastDiceRoll(),
                            currentPlayer.getName(),
                            currentPlayerLastCell,
                            currentPlayerStatus.getCurrentCell(),
                            currentPlayerStatus.getCurrentCell(),
                            player.getName(),
                            currentPlayerLastCell==0?"Start":currentPlayerLastCell));
                }
            }
        });
    }
}
