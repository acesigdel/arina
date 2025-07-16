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
import com.arinax.playloads.GameModeDto;
import com.arinax.services.GameModeService;
import com.arinax.services.GameService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/modes")
public class GameModeController {

	@Autowired
	private GameModeService modeService;

	// create

	@PostMapping("/")
	public ResponseEntity<GameModeDto> createGameMode(@Valid @RequestBody GameModeDto gameModeDto) {
		GameModeDto createMode = this.modeService.createGameMode(gameModeDto);
		return new ResponseEntity<GameModeDto>(createMode, HttpStatus.CREATED);
	}

	// update

	@PutMapping("/{modeId}")
	public ResponseEntity<GameModeDto> updateGameMode(@Valid @RequestBody GameModeDto gameModeDto,
			@PathVariable("modeId") Integer modeId) {
		GameModeDto updatedGame = this.modeService.updateGameMode(gameModeDto,modeId);
		return new ResponseEntity<GameModeDto>(updatedGame, HttpStatus.OK);
	}
	


	// delete

	@DeleteMapping("/{modeId}")
	public ResponseEntity<ApiResponse> deleteGameMode(@PathVariable Integer modeId) {
		this.modeService.deleteGameMode(modeId);
		return new ResponseEntity<ApiResponse>(new ApiResponse("GameMode is deleted successfully !!", true),
				HttpStatus.OK);
	}
	// get

	@GetMapping("/{modeId}")
	public ResponseEntity<GameModeDto> getGameMode(@PathVariable Integer modeId) {

		GameModeDto gameModeDto = this.modeService.getGameMode(modeId);

		return new ResponseEntity<GameModeDto>(gameModeDto, HttpStatus.OK);

	}

	// get all
	@GetMapping("/")
	public ResponseEntity<List<GameModeDto>> getGameModes() {
		List<GameModeDto> games = this.modeService.getGameModes();
		return ResponseEntity.ok(games);
	}

}
