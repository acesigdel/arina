package com.arinax.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arinax.entities.Game;
import com.arinax.exceptions.ResourceNotFoundException;
import com.arinax.playloads.GameDto;
import com.arinax.repositories.GameRepo;
import com.arinax.services.GameService;
import org.springframework.util.StringUtils;

@Service
public class GameServiceImpl implements GameService {

	@Autowired
	private GameRepo gameRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public GameDto createGame(GameDto gameDto) {
		Game cat = this.modelMapper.map(gameDto, Game.class);
		Game addedCat = this.gameRepo.save(cat);
		return this.modelMapper.map(addedCat, GameDto.class);
	}

	@Override
	public GameDto updateGame(GameDto gameDto, Integer gameId) {
	    Game game = this.gameRepo.findById(gameId)
	            .orElseThrow(() -> new ResourceNotFoundException("Game", "Game Id", gameId));

	    if (StringUtils.hasText(gameDto.getGameTitle())) {
	        game.setGameTitle(gameDto.getGameTitle());
	    }
	    if (StringUtils.hasText(gameDto.getGameDescription())) {
	        game.setGameDescription(gameDto.getGameDescription());
	    }

	    Game updatedGame = this.gameRepo.save(game);
	    return this.modelMapper.map(updatedGame, GameDto.class);
	}


	
	//Delete Game
	@Override
	public void deleteGame(Integer gameId) {

		Game cat = this.gameRepo.findById(gameId)
				.orElseThrow(() -> new ResourceNotFoundException("Game ", "game id", gameId));
		this.gameRepo.delete(cat);
	}

	
	//GetCAtegory BY Id
	@Override
	public GameDto getGame(Integer gameId) {
		Game cat = this.gameRepo.findById(gameId)
				.orElseThrow(() -> new ResourceNotFoundException("Game", "game id", gameId));

		return this.modelMapper.map(cat, GameDto.class);
	}

	//GetAll CAtegory
	@Override
	public List<GameDto> getGames() {

		List<Game> games = this.gameRepo.findAll();
		List<GameDto> catDtos = games.stream().map((cat) -> this.modelMapper.map(cat, GameDto.class))
				.collect(Collectors.toList());

		return catDtos;
	}

}
