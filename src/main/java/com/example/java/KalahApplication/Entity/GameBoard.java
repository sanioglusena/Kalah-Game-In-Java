package com.example.java.KalahApplication.Entity;

import java.io.Serializable;
import java.util.HashMap;

public class GameBoard implements Serializable {

    private Long id;

    private HashMap<Integer, Integer> pits;

    private boolean firstPersonsTurn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HashMap<Integer, Integer> getPits() {
        return pits;
    }

    public void setPits(HashMap<Integer, Integer> pits) {
        this.pits = pits;
    }

    public boolean isFirstPersonsTurn() {
        return firstPersonsTurn;
    }

    public boolean isSecondPersonsTurn() {
        return !firstPersonsTurn;
    }

    public void setFirstPersonsTurn(boolean firstPersonsTurn) {
        this.firstPersonsTurn = firstPersonsTurn;
    }
}
