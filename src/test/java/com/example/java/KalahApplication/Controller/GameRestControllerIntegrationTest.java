package com.example.java.KalahApplication.Controller;

import com.example.java.KalahApplication.Entity.GameBoard;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GameRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private GameBoard gameBoard;

    private String uri = "localhost:8080/games/";

    @Before
    public void setup() {
        initializeGameBoard();
    }

    @Test
    public void shouldCreateGameAndReturnHttpStatus201() throws Exception {
        mockMvc.perform(post("/games")).andExpect(status().isCreated());

    }

    @Test
    public void shouldMoveWhenMoveIsValidAndReturn() throws Exception {
        mockMvc.perform(put("/games/0/pits/1")).andExpect(status().isOk());
    }

    @Test
    public void shouldNotMoveWhenMoveIsNotValid() throws Exception {
        mockMvc.perform(put("/games/0/pits/13")).andExpect(status().isBadRequest());
    }

    private void initializeGameBoard() {
        gameBoard = new GameBoard();
        gameBoard.setFirstPersonsTurn(true);

        HashMap<Integer, Integer> pits = new HashMap<>();
        int i;
        for (i = 1; i <= 14; i++) {
            pits.put(i, 6);
        }

        pits.put(7, 0);
        pits.put(14, 0);

        gameBoard.setPits(pits);
    }
}