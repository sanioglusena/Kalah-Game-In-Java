package com.example.java.KalahApplication.Response;

import java.util.HashMap;

public class MoveResponse {

    private HashMap<Integer, Integer> gameBoardPits;

    public HashMap<Integer, Integer> getGameBoardPits() {
        return gameBoardPits;
    }

    public void setGameBoardPits(HashMap<Integer, Integer> gameBoardPits) {
        this.gameBoardPits = gameBoardPits;
    }
}
