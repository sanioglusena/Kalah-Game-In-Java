package com.example.java.KalahApplication.Service;

import com.example.java.KalahApplication.Entity.GameBoard;
import com.example.java.KalahApplication.Repository.GameBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GameService {

    @Autowired
    private GameBoardRepository gameBoardRepository;

    public GameBoard createGame() {
        GameBoard gameBoard = initializeGameBoard();
        return gameBoardRepository.save(gameBoard);
    }

    public GameBoard move(Long gameId, Integer pitId) {
        GameBoard gameBoard = gameBoardRepository.find(gameId);
        HashMap<Integer, Integer> pitMap = gameBoard.getPits();

        //take all seeds from current pit
        Integer seedCountInCurrentPit = pitMap.get(pitId);
        pitMap.put(pitId, 0);

        int nextPit, moveCount;
        nextPit = (pitId + 1);
        for (moveCount = 0; moveCount < seedCountInCurrentPit; moveCount++) {

            Integer seedCountInNextPit = pitMap.get(nextPit);

            if (isNotOppositePlayersKalah(gameBoard, nextPit)) {
                if (isNextPitEmptyAndPitBelongsToPlayer(nextPit, gameBoard) && isNextPitNotKalah(nextPit)) {
                    takeOpponentsSeedsFromOppositePit(gameBoard, nextPit);
                } else {
                    pitMap.put(nextPit, seedCountInNextPit + 1);
                }
            }

            /**
             * Check if player gets another round
             */
            if (isFinalMove(seedCountInCurrentPit, moveCount + 1)) {
                gameBoard = setPlayerTurn(gameBoard, nextPit);
            }

            /**
             * Check if game finished
             */
            if (isFirstPersonFinished(gameBoard)) {
                removeSecondPersonsPits(gameBoard);
                break;
            } else if (isSecondPersonFinished(gameBoard)) {
                removeFirstPersonsPits(gameBoard);
                break;
            }

            nextPit = getNextPit(gameBoard, nextPit);
        }

        gameBoard.setPits(pitMap);

        gameBoardRepository.update(gameBoard);

        return gameBoard;
    }

    /**
     * Decides which player plays next
     *
     * @param gameBoard
     * @param nextPit
     * @return
     */
    private GameBoard setPlayerTurn(GameBoard gameBoard, int nextPit) {
        if (nextPit != 7 && gameBoard.isFirstPersonsTurn()) {
            gameBoard.setFirstPersonsTurn(false);
        } else if (nextPit == 7 && gameBoard.isFirstPersonsTurn()) {
            gameBoard.setFirstPersonsTurn(true);
        } else if (nextPit != 14 && gameBoard.isSecondPersonsTurn()) {
            gameBoard.setFirstPersonsTurn(true);
        } else if (nextPit == 14 && gameBoard.isSecondPersonsTurn()) {
            gameBoard.setFirstPersonsTurn(false);
        }
        return gameBoard;
    }

    private boolean isFinalMove(Integer seedCountInCurrentPit, int i) {
        return i == seedCountInCurrentPit;
    }

    /**
     * Gets next pit position to play.
     *
     * @param gameBoard
     * @param nextPit
     * @return
     */
    private int getNextPit(GameBoard gameBoard, int nextPit) {
        nextPit++;
        // First player can not play pit 14
        if ((nextPit == 14 && gameBoard.isFirstPersonsTurn())) {
            nextPit = 1;
        }
        // Second player can play its kalah
        else if ((nextPit == 14 && gameBoard.isSecondPersonsTurn())) {
            nextPit = 14;
        }
        // Pit number must be smaller than 14
        else {
            nextPit = (nextPit) % 14;
        }
        return nextPit;
    }

    private void removeSecondPersonsPits(GameBoard gameBoard) {
        HashMap<Integer, Integer> pitMap = gameBoard.getPits();
        for (Map.Entry<Integer, Integer> entry : pitMap.entrySet()) {
            if (entry.getKey() >= 8 && entry.getKey() <= 13) {
                pitMap.put(14, pitMap.get(14) + entry.getValue());
                pitMap.put(entry.getKey(), 0);
            }
        }
        gameBoard.setPits(pitMap);
    }

    private void removeFirstPersonsPits(GameBoard gameBoard) {
        HashMap<Integer, Integer> pitMap = gameBoard.getPits();
        for (Map.Entry<Integer, Integer> entry : pitMap.entrySet()) {
            if (entry.getKey() < 7) {
                pitMap.put(7, pitMap.get(7) + entry.getValue());
                pitMap.put(entry.getKey(), 0);
            }
        }
        gameBoard.setPits(pitMap);

    }

    /**
     * Checks if the pit is the other player's kalah.
     * Player's can not leave seeds to the other player's kalah.
     *
     * @param gameBoard
     * @param nextPit
     * @return
     */
    private boolean isNotOppositePlayersKalah(GameBoard gameBoard, int nextPit) {
        return (gameBoard.isFirstPersonsTurn() && nextPit != 14) || (gameBoard.isSecondPersonsTurn() && nextPit != 7);
    }

    /**
     * Returns if next pit is empty.
     * If next pit is empty, player takes all of opponents seeds from the opposite pit.
     *
     * @param nextPit
     * @param gameBoard
     * @return
     */
    private boolean isNextPitEmptyAndPitBelongsToPlayer(Integer nextPit, GameBoard gameBoard) {
        return gameBoard.getPits().get(nextPit) == 0 && ((gameBoard.isFirstPersonsTurn() && nextPit < 7) || (gameBoard.isSecondPersonsTurn() && nextPit < 14 && nextPit > 7));
    }

    /**
     * Moves for next pit empty case.
     * Player gets the seeds from opposite pit to its own kalah.
     *
     * @param gameBoard
     * @param nextPit
     */
    private void takeOpponentsSeedsFromOppositePit(GameBoard gameBoard, int nextPit) {
        HashMap<Integer, Integer> pitMap = gameBoard.getPits();
        Integer positionOfOppositePit = 14 - nextPit;
        Integer seedsOfOpponent = pitMap.get(positionOfOppositePit);
        Integer kalahPositionOfPlayer = getKalahPosition(gameBoard);
        pitMap.put(kalahPositionOfPlayer, getPlayersKalahSeedCount(pitMap, kalahPositionOfPlayer) + seedsOfOpponent + 1);
        pitMap.put(positionOfOppositePit, 0);

    }

    private boolean isNextPitNotKalah(Integer nextPit) {
        return (nextPit != 7 && nextPit != 14);
    }

    /**
     * Returns player's kalah seed count.
     *
     * @param pitMap
     * @param kalahPositionOfPlayer
     * @return
     */
    private Integer getPlayersKalahSeedCount(HashMap<Integer, Integer> pitMap, Integer kalahPositionOfPlayer) {
        return pitMap.get(kalahPositionOfPlayer);
    }

    /**
     * Returns the kalah of the current player.
     *
     * @param gameBoard
     * @return
     */
    private int getKalahPosition(GameBoard gameBoard) {
        return gameBoard.isFirstPersonsTurn() ? 7 : 14;
    }

    /**
     * Initialize game board.
     * South person is the first person by default.
     *
     * @return
     */
    private GameBoard initializeGameBoard() {
        GameBoard gameBoard = new GameBoard();
        HashMap<Integer, Integer> pits = initializePits();
        gameBoard.setPits(pits);
        gameBoard.setFirstPersonsTurn(true);
        gameBoard.setId(gameBoardRepository.getUniqueId());
        return gameBoard;
    }

    /**
     * Initializes game board for 6 stone Kalah game.
     *
     * @return HashMap
     */
    private HashMap<Integer, Integer> initializePits() {
        HashMap<Integer, Integer> pits = new HashMap<>();
        int i;
        for (i = 1; i <= 14; i++) {
            pits.put(i, 6);
        }

        pits.put(7, 0);
        pits.put(14, 0);
        return pits;
    }

    /**
     * Checks if game is finished by first person.
     *
     * @param gameBoard
     * @return
     */
    private boolean isFirstPersonFinished(GameBoard gameBoard) {
        HashMap<Integer, Integer> pitMap = gameBoard.getPits();
        return pitMap.entrySet().stream().filter(map -> map.getKey() <= 6).filter(map -> map.getValue() != 0).collect(Collectors.toList()).size() == 0;
    }

    /**
     * Checks if game is finished by second person.
     *
     * @param gameBoard
     * @return
     */
    private boolean isSecondPersonFinished(GameBoard gameBoard) {
        HashMap<Integer, Integer> pitMap = gameBoard.getPits();
        return pitMap.entrySet().stream().filter(map -> (map.getKey() >= 8 && map.getKey() <= 13)).filter(map -> map.getValue() != 0).collect(Collectors.toList()).size() == 0;
    }
}
