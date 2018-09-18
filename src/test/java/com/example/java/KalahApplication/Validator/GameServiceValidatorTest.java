package com.example.java.KalahApplication.Validator;

import com.example.java.KalahApplication.Entity.GameBoard;
import com.example.java.KalahApplication.Exception.KalahException;
import com.example.java.KalahApplication.Repository.GameBoardRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceValidatorTest {

    @Mock
    private GameBoardRepository gameBoardRepository;

    @InjectMocks
    private GameServiceValidator gameServiceValidator;

    private HashMap<Integer, Integer> initialPits;
    private GameBoard gameBoard;

    @Before
    public void setUp() {
        gameBoard = new GameBoard();
        gameBoard.setFirstPersonsTurn(true);
        initialPits = initializePits();
        gameBoard.setPits(initialPits);
    }

    @Test
    public void shouldValidateGame() {
        when(gameBoardRepository.find(13L)).thenReturn(gameBoard);
        gameServiceValidator.validateGameId(13L);
        verify(gameBoardRepository).find(13L);
    }

    @Test
    public void shouldNotValidateGameWhenGameDoesNotExist() {
        try {
            gameServiceValidator.validateGameId(14L);
        } catch (KalahException e) {
            verify(gameBoardRepository).find(13L);
        }
    }

    @Test
    public void shouldNotValidateGameWhenFirstPlayerTriesToPlayEvenItsSecondPlayersTurn() {
        gameBoard.setFirstPersonsTurn(false);
        try {
            when(gameBoardRepository.find(13L)).thenReturn(gameBoard);
            gameServiceValidator.validateMove(13L, 5);
        } catch (KalahException e) {
            verify(gameBoardRepository).find(13L);
        }
    }

    @Test
    public void shouldNotValidateGameWhenSecondPlayerTriesToPlayEvenItsFirstPlayersTurn() {
        try {
            when(gameBoardRepository.find(13L)).thenReturn(gameBoard);
            gameServiceValidator.validateMove(13L, 14);
        } catch (KalahException e) {
            verify(gameBoardRepository).find(13L);
        }
    }

    private HashMap<Integer, Integer> initializePits() {
        initialPits = new HashMap<>();
        int i;
        for (i = 1; i <= 14; i++) {
            initialPits.put(i, 6);
        }

        initialPits.put(7, 0);
        initialPits.put(14, 0);
        return initialPits;
    }

}