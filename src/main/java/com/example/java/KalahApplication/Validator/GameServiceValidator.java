package com.example.java.KalahApplication.Validator;

import com.example.java.KalahApplication.Entity.GameBoard;
import com.example.java.KalahApplication.Exception.KalahException;
import com.example.java.KalahApplication.Repository.GameBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameServiceValidator {

    @Autowired
    private GameBoardRepository gameBoardRepository;

    public void validateMove(Long gameId, Integer pitId) {
        GameBoard gameBoard = validateGameId(gameId);
        validatePlayersMove(gameBoard, pitId);

    }

    private void validatePlayersMove(GameBoard gameBoard, Integer pitId) {
        if ((gameBoard.isFirstPersonsTurn() && pitId >= 7) || (gameBoard.isSecondPersonsTurn() && (pitId < 7 || pitId == 14))) {
            throw new KalahException("Move is not valid");
        }
    }

    public GameBoard validateGameId(Long gameId) {
        try {
            return gameBoardRepository.find(gameId);
        } catch (Exception e) {
            throw new KalahException("GameId is not valid!");
        }
    }
}
