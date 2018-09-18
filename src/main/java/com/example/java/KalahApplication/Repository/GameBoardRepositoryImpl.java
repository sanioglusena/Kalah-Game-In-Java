package com.example.java.KalahApplication.Repository;

import com.example.java.KalahApplication.Entity.GameBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
public class GameBoardRepositoryImpl implements GameBoardRepository {

    private static final String KEY = "GameBoard";
    private Long idCounter;

    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, Long, GameBoard> hashOperations;

    @Autowired
    public GameBoardRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        idCounter = 0L;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public GameBoard save(GameBoard gameBoard) {
        hashOperations.put(KEY, gameBoard.getId(), gameBoard);
        setIdCounter(getIdCounter() + 1L);
        return gameBoard;
    }

    @Override
    public GameBoard find(Long id) {
        return hashOperations.get(KEY, id);
    }

    @Override
    public void update(GameBoard gameBoard) {
        hashOperations.put(KEY, gameBoard.getId(), gameBoard);
    }

    @Override
    public Long getUniqueId() {
        return getIdCounter();
    }

    public Long getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(Long idCounter) {
        this.idCounter = idCounter;
    }
}
