package com.example.java.KalahApplication.Service;

import com.example.java.KalahApplication.Entity.GameBoard;
import com.example.java.KalahApplication.Repository.GameBoardRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

    @Mock
    private GameBoardRepository gameBoardRepository;

    @InjectMocks
    private GameService gameService;

    @Captor
    private ArgumentCaptor<GameBoard> gameBoardArgumentCaptor;

    private HashMap<Integer, Integer> initialPits;
    private GameBoard gameBoard;

    @Before
    public void setUp() {
        gameBoard = new GameBoard();
        gameBoard.setFirstPersonsTurn(true);
        initialPits = initializePits();
        gameBoard.setPits(initialPits);
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

    @Test
    public void shouldCreateGame() {
        when(gameBoardRepository.getUniqueId()).thenReturn(13L);

        gameService.createGame();

        verify(gameBoardRepository).getUniqueId();
        verify(gameBoardRepository).save(gameBoardArgumentCaptor.capture());

        GameBoard gameBoard = gameBoardArgumentCaptor.getValue();
        assertThat(gameBoard.isFirstPersonsTurn()).isEqualTo(true);
        assertThat(gameBoard.getId()).isEqualTo(13L);
        assertThat(gameBoard.getPits()).isEqualTo(initialPits);
    }

    @Test
    public void shouldMove() {
        when(gameBoardRepository.find(13L)).thenReturn(gameBoard);

        gameService.move(13L, 4);

        verify(gameBoardRepository).find(13L);
        verify(gameBoardRepository).update(gameBoardArgumentCaptor.capture());

        HashMap<Integer, Integer> pitsAfterMove = initializePits();
        pitsAfterMove.put(4, 0);
        pitsAfterMove.put(5, 7);
        pitsAfterMove.put(6, 7);
        pitsAfterMove.put(7, 1);
        pitsAfterMove.put(8, 7);
        pitsAfterMove.put(9, 7);
        pitsAfterMove.put(10, 7);

        GameBoard gameBoardUpdated = gameBoardArgumentCaptor.getValue();

        assertThat(gameBoardUpdated.getPits()).isEqualTo(pitsAfterMove);
        assertThat(gameBoardUpdated.isSecondPersonsTurn()).isEqualTo(true);
    }

    @Test
    public void shouldMoveWhenPitIsEmptyAndTakeSeedsFromOppositePit() {
        HashMap<Integer, Integer> nextPitEmptyMap = initialPits;
        nextPitEmptyMap.put(6, 0);
        gameBoard.setPits(nextPitEmptyMap);

        when(gameBoardRepository.find(13L)).thenReturn(gameBoard);

        gameService.move(13L, 5);

        verify(gameBoardRepository).find(13L);
        verify(gameBoardRepository).update(gameBoardArgumentCaptor.capture());

        HashMap<Integer, Integer> pitsAfterMove = initializePits();
        pitsAfterMove.put(5, 0);
        pitsAfterMove.put(6, 0);
        pitsAfterMove.put(7, 8);
        pitsAfterMove.put(8, 1);
        pitsAfterMove.put(9, 7);
        pitsAfterMove.put(10, 7);
        pitsAfterMove.put(11, 7);

        GameBoard gameBoardUpdated = gameBoardArgumentCaptor.getValue();

        assertThat(gameBoardUpdated.getPits()).isEqualTo(pitsAfterMove);
        assertThat(gameBoardUpdated.isSecondPersonsTurn()).isEqualTo(true);
    }

    @Test
    public void shouldSkipOpponentsKalah() {
        HashMap<Integer, Integer> nextPitEmptyMap = initialPits;
        nextPitEmptyMap.put(6, 12);
        gameBoard.setPits(nextPitEmptyMap);

        when(gameBoardRepository.find(13L)).thenReturn(gameBoard);

        gameService.move(13L, 6);

        verify(gameBoardRepository).find(13L);
        verify(gameBoardRepository).update(gameBoardArgumentCaptor.capture());

        HashMap<Integer, Integer> pitsAfterMove = initializePits();
        pitsAfterMove.put(1, 7);
        pitsAfterMove.put(2, 7);
        pitsAfterMove.put(3, 7);
        pitsAfterMove.put(4, 7);
        pitsAfterMove.put(5, 7);
        pitsAfterMove.put(6, 0);
        pitsAfterMove.put(7, 1);
        pitsAfterMove.put(8, 7);
        pitsAfterMove.put(9, 7);
        pitsAfterMove.put(10, 7);
        pitsAfterMove.put(11, 7);
        pitsAfterMove.put(12, 7);
        pitsAfterMove.put(13, 7);
        pitsAfterMove.put(14, 0);

        GameBoard gameBoardUpdated = gameBoardArgumentCaptor.getValue();

        assertThat(gameBoardUpdated.getPits()).isEqualTo(pitsAfterMove);
        assertThat(gameBoardUpdated.isSecondPersonsTurn()).isEqualTo(true);

    }

    @Test
    public void shouldRemoveSecondPlayersPitsWhenFirstPlayerFinishesPits() {
        HashMap<Integer, Integer> nextPitEmptyMap = initialPits;

        nextPitEmptyMap.put(1, 0);
        nextPitEmptyMap.put(2, 0);
        nextPitEmptyMap.put(3, 0);
        nextPitEmptyMap.put(4, 0);
        nextPitEmptyMap.put(5, 0);

        gameBoard.setPits(nextPitEmptyMap);

        when(gameBoardRepository.find(13L)).thenReturn(gameBoard);

        gameService.move(13L, 6);

        verify(gameBoardRepository).find(13L);
        verify(gameBoardRepository).update(gameBoardArgumentCaptor.capture());

        HashMap<Integer, Integer> pitsAfterMove = initializePits();
        pitsAfterMove.put(1, 0);
        pitsAfterMove.put(2, 0);
        pitsAfterMove.put(3, 0);
        pitsAfterMove.put(4, 0);
        pitsAfterMove.put(5, 0);
        pitsAfterMove.put(6, 0);
        pitsAfterMove.put(7, 1);
        pitsAfterMove.put(8, 0);
        pitsAfterMove.put(9, 0);
        pitsAfterMove.put(10, 0);
        pitsAfterMove.put(11, 0);
        pitsAfterMove.put(12, 0);
        pitsAfterMove.put(13, 0);
        pitsAfterMove.put(14, 36);

        GameBoard gameBoardUpdated = gameBoardArgumentCaptor.getValue();

        assertThat(gameBoardUpdated.getPits()).isEqualTo(pitsAfterMove);
        assertThat(gameBoardUpdated.isSecondPersonsTurn()).isEqualTo(false);

    }

    @Test
    public void shouldRemoveFirstPlayersPitsWhenFirstSecondPlayerFinishesPits() {
        HashMap<Integer, Integer> nextPitEmptyMap = initialPits;

        nextPitEmptyMap.put(8, 0);
        nextPitEmptyMap.put(9, 0);
        nextPitEmptyMap.put(10, 0);
        nextPitEmptyMap.put(11, 0);
        nextPitEmptyMap.put(12, 0);

        gameBoard.setPits(nextPitEmptyMap);
        gameBoard.setFirstPersonsTurn(false);

        when(gameBoardRepository.find(13L)).thenReturn(gameBoard);

        gameService.move(13L, 13);

        verify(gameBoardRepository).find(13L);
        verify(gameBoardRepository).update(gameBoardArgumentCaptor.capture());

        HashMap<Integer, Integer> pitsAfterMove = initializePits();
        pitsAfterMove.put(1, 0);
        pitsAfterMove.put(2, 0);
        pitsAfterMove.put(3, 0);
        pitsAfterMove.put(4, 0);
        pitsAfterMove.put(5, 0);
        pitsAfterMove.put(6, 0);
        pitsAfterMove.put(7, 36);
        pitsAfterMove.put(8, 0);
        pitsAfterMove.put(9, 0);
        pitsAfterMove.put(10, 0);
        pitsAfterMove.put(11, 0);
        pitsAfterMove.put(12, 0);
        pitsAfterMove.put(13, 0);
        pitsAfterMove.put(14, 1);

        GameBoard gameBoardUpdated = gameBoardArgumentCaptor.getValue();

        assertThat(gameBoardUpdated.getPits()).isEqualTo(pitsAfterMove);
        assertThat(gameBoardUpdated.isSecondPersonsTurn()).isEqualTo(true);

    }
}