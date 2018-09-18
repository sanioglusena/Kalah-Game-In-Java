package com.example.java.KalahApplication.Repository;

import com.example.java.KalahApplication.Entity.GameBoard;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameBoardRepositoryIntegrationTest {

    private RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, Long, GameBoard> hashOperations;

    @Autowired
    private GameBoardRepositoryImpl gameBoardRepository;

    private GameBoard gameBoard;

    @Before
    public void setUp() {
        gameBoard = new GameBoard();
        gameBoard.setId(3L);
        gameBoard.setFirstPersonsTurn(true);

        redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(new JedisConnectionFactory());

        redisTemplate.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        hashOperations = redisTemplate.opsForHash();
    }

    @Test
    public void shouldSaveGameBoard() {
        GameBoard savedGameBoard = gameBoardRepository.save(gameBoard);
        assertThat(savedGameBoard.getId()).isEqualTo(3L);
    }

    @Test
    public void shoulFindGameBoard() {
        gameBoardRepository.save(gameBoard);
        GameBoard foundGameBoard = gameBoardRepository.find(3L);
        assertThat(foundGameBoard.isFirstPersonsTurn()).isEqualTo(true);
    }

    @Test
    public void shouldUpdateGameBoardWhenPlayerTurnIsChanged() {
        gameBoard.setFirstPersonsTurn(false);
        gameBoardRepository.update(gameBoard);
        GameBoard foundGameBoard = gameBoardRepository.find(3L);
        assertThat(foundGameBoard.isSecondPersonsTurn()).isEqualTo(true);
    }

    @Test
    public void shouldUpdateGameBoardWhenPitsAreChanged() {
        HashMap<Integer, Integer> newPits = new HashMap<>();
        newPits.put(1, 6);
        gameBoard.setPits(newPits);
        gameBoardRepository.update(gameBoard);
        GameBoard foundGameBoard = gameBoardRepository.find(3L);
        assertThat(foundGameBoard.isFirstPersonsTurn()).isEqualTo(true);
        assertThat(foundGameBoard.getPits()).isEqualTo(newPits);
    }
}