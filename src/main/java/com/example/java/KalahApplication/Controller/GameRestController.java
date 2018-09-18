package com.example.java.KalahApplication.Controller;

import com.example.java.KalahApplication.Entity.GameBoard;
import com.example.java.KalahApplication.Response.GamesResponse;
import com.example.java.KalahApplication.Response.MoveResponse;
import com.example.java.KalahApplication.Service.GameService;
import com.example.java.KalahApplication.Validator.GameServiceValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("games")
@RestController
public class GameRestController {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameServiceValidator gameServiceValidator;

    @Value("${server.context-path}")
    private String uri;

    @PostMapping
    public ResponseEntity game() {
        GameBoard gameBoard = gameService.createGame();
        GamesResponse gamesResponse = new GamesResponse();
        gamesResponse.setGameId(gameBoard.getId());
        gamesResponse.setGameUri(uri + gameBoard.getId());
        ResponseEntity responseEntity = new ResponseEntity(gamesResponse, HttpStatus.CREATED);
        return responseEntity;
    }

    @PutMapping("{gameId}/pits/{pitId}")
    public ResponseEntity move(@PathVariable Long gameId, @PathVariable Integer pitId) {
        gameServiceValidator.validateMove(gameId, pitId);
        GameBoard gameBoard = gameService.move(gameId, pitId);
        MoveResponse moveResponse = new MoveResponse();
        moveResponse.setGameBoardPits(gameBoard.getPits());
        ResponseEntity responseEntity = new ResponseEntity(moveResponse, HttpStatus.OK);
        return responseEntity;
    }
}
