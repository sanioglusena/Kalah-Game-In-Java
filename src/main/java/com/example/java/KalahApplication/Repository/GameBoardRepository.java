package com.example.java.KalahApplication.Repository;

import com.example.java.KalahApplication.Entity.GameBoard;

public interface GameBoardRepository {

    public GameBoard save(GameBoard gameBoard);

    public GameBoard find(Long id);

    public void update(GameBoard gameBoard);

    public Long getUniqueId();
}
