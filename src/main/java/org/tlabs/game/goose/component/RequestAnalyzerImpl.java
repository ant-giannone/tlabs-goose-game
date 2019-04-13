package org.tlabs.game.goose.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.tlabs.game.goose.component.strategy.analyzer.RequestAnalyzerStrategy;
import org.tlabs.game.goose.component.strategy.analyzer.RequestAnalyzerWithDiceStrategy;
import org.tlabs.game.goose.component.strategy.analyzer.RequestAnalyzerWithoutDiceStrategy;
import org.tlabs.game.goose.domain.Player;
import org.tlabs.game.goose.domain.PlayerStatus;
import org.tlabs.game.goose.exception.UnknownPlayerException;
import org.tlabs.game.goose.exception.UnknownRequestFormatException;
import org.tlabs.game.goose.exception.UnknownStrategyException;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class RequestAnalyzerImpl implements RequestAnalyzer {

    protected HashMap<String, RequestAnalyzerStrategy> movePlayerStrategyHashMap;

    public RequestAnalyzerImpl() {
        movePlayerStrategyHashMap = new HashMap<>();
    }

    private String[] preAnalyzeRequest(final String request) throws UnknownRequestFormatException {

        if(StringUtils.isEmpty(request)) {
            throw new UnknownRequestFormatException("Unable to understand request grammar: data is empty");
        }

        final String normalizedRequest = StringUtils.normalizeSpace(request);

        if(StringUtils.isEmpty(normalizedRequest)) {
            throw new UnknownRequestFormatException("Unable to understand request grammar: normalized data is empty");
        }

        return normalizedRequest.split(" ");
    }

    @Override
    public KeyTerms getKeyTerm(final String request)  throws UnknownRequestFormatException {

        String[] terms = preAnalyzeRequest(request);

        if(terms==null || terms.length<=1) {
            throw new UnknownRequestFormatException("Unable to understand request grammar: insufficient terms");
        }

        if("add".equals(terms[0])) {
            return KeyTerms.ADD;
        }else if("move".equals(terms[0])) {
            return KeyTerms.MOVE;
        }

        throw new UnknownRequestFormatException("Unable to understand request grammar: any key-term found");
    }

    @Override
    public String doYouWantAddPlayer(final String request) throws UnknownRequestFormatException {

        String[] terms = preAnalyzeRequest(request);

        if(terms==null || terms.length!=3) {
            throw new UnknownRequestFormatException("Unable to understand request grammar: terms counter doesn't match");
        }

        if(!("add".equals(terms[0]) && "player".equals(terms[1]))) {

            throw new UnknownRequestFormatException("Unable to understand request grammar: grammar not correspond");
        }

        if(StringUtils.isEmpty(terms[2])) {

            throw new UnknownRequestFormatException("Unable to understand request grammar: player's name missed");
        }

        return terms[2];
    }

    @Override
    public boolean doYouDigitQuit(final String request) throws UnknownRequestFormatException {

        String[] terms = preAnalyzeRequest(request);

        if(terms==null || terms.length!=1) {
            throw new UnknownRequestFormatException("Unable to understand request grammar: terms counter doesn't match");
        }

        return "quit".equals(terms[0]);
    }

    @Override
    public Pair<Player, PlayerStatus> howManyStepFor(final Set<Player> players, final String request)
            throws UnknownRequestFormatException, UnknownPlayerException, UnknownStrategyException {

        String[] terms = preAnalyzeRequest(request);

        if(terms==null || terms.length>3) {
            throw new UnknownRequestFormatException("Unable to understand request grammar: terms counter doesn't match");
        }

        RequestAnalyzerStrategy requestAnalyzerStrategy = getMovePlayerStrategy(terms);

        PlayerStatus playerStatus = requestAnalyzerStrategy.execute(terms);

        Optional<Player> player = getPlayer(players, terms[1]);

        if(!player.isPresent())
            throw new UnknownPlayerException("Player not found with name: " + terms[1]);

        return new ImmutablePair<>(player.get(), playerStatus);
    }

    private Optional<Player> getPlayer(final Set<Player> players, String name) {

        return players.stream().filter(player -> player.getName().equals(name)).findFirst();
    }

    private RequestAnalyzerStrategy getMovePlayerStrategy(String[] terms) throws UnknownStrategyException {

        if(terms.length==2) {

            if(!movePlayerStrategyHashMap.containsKey(RequestAnalyzerWithDiceStrategy.class.getName())) {

                movePlayerStrategyHashMap.put(
                        RequestAnalyzerWithDiceStrategy.class.getName(),
                        new RequestAnalyzerWithDiceStrategy()
                );
            }

            return movePlayerStrategyHashMap.get(RequestAnalyzerWithDiceStrategy.class.getName());
        }else if(terms.length==3) {

            if(!movePlayerStrategyHashMap.containsKey(RequestAnalyzerWithoutDiceStrategy.class.getName())) {

                movePlayerStrategyHashMap.put(
                        RequestAnalyzerWithoutDiceStrategy.class.getName(),
                        new RequestAnalyzerWithoutDiceStrategy()
                );
            }

            return movePlayerStrategyHashMap.get(RequestAnalyzerWithoutDiceStrategy.class.getName());
        }

        throw new UnknownStrategyException("Unknown strategy for RequestAnalyzerStrategy implementation");
    }
}
