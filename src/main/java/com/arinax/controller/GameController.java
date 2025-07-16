package com.arinax.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arinax.playloads.ApiResponse;
import com.arinax.playloads.GameDto;
import com.arinax.services.GameService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/games")
public class GameController {

	@Autowired
	private GameService gameService;

	// create

	@PostMapping("/")
	public ResponseEntity<GameDto> createCategory(@Valid @RequestBody GameDto gameDto) {
		GameDto createGame = this.gameService.createGame(gameDto);
		return new ResponseEntity<GameDto>(createGame, HttpStatus.CREATED);
	}

	// update

	@PutMapping("/{gaId}")
	public ResponseEntity<GameDto> updateGame(@Valid @RequestBody GameDto gameDto,
	        @PathVariable("gaId") Integer gaId) {
	    GameDto updatedGame = this.gameService.updateGame(gameDto, gaId);
	    return new ResponseEntity<>(updatedGame, HttpStatus.OK);
	}


	// delete

	@DeleteMapping("/{gaId}")
	public ResponseEntity<ApiResponse> deleteGame(@PathVariable Integer gaId) {
		this.gameService.deleteGame(gaId);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Game is deleted successfully !!", true),
				HttpStatus.OK);
	}
	// get

	@GetMapping("/{gaId}")
	public ResponseEntity<GameDto> getGame(@PathVariable Integer gaId) {

		GameDto gameDto = this.gameService.getGame(gaId);

		return new ResponseEntity<GameDto>(gameDto, HttpStatus.OK);

	}

	// get all
	@GetMapping("/")
	public ResponseEntity<List<GameDto>> getGames() {
		List<GameDto> games = this.gameService.getGames();
		return ResponseEntity.ok(games);
	}

}
