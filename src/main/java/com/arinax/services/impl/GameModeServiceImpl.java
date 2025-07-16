package com.arinax.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.arinax.entities.GameMode;
import com.arinax.exceptions.ResourceNotFoundException;

import com.arinax.playloads.GameModeDto;
import com.arinax.repositories.GameModeRepo;

import com.arinax.services.GameModeService;

@Service
public class GameModeServiceImpl implements GameModeService{

	@Autowired
	private GameModeRepo gameModeRepo;

	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public GameModeDto createGameMode(GameModeDto gameModeDto) {
		GameMode cat = this.modelMapper.map(gameModeDto, GameMode.class);
		GameMode addedMo = this.gameModeRepo.save(cat);
		return this.modelMapper.map(addedMo, GameModeDto.class);
	}

	@Override
	public GameModeDto updateGameMode(GameModeDto gameModeDto, Integer modeId) {
		GameMode cat = this.gameModeRepo.findById(modeId)
				.orElseThrow(() -> new ResourceNotFoundException("GameMode ", "Mode Id", modeId));
		if(StringUtils.hasText(gameModeDto.getModeName())) {
			cat.setModeName(gameModeDto.getModeName());
		}
		
		GameMode updatedmode = this.gameModeRepo.save(cat);
		return this.modelMapper.map(updatedmode, GameModeDto.class);
	}
	


	@Override
	public void deleteGameMode(Integer modeId) {
		GameMode cat = this.gameModeRepo.findById(modeId)
				.orElseThrow(() -> new ResourceNotFoundException("GameMode ", "mode id", modeId));
		this.gameModeRepo.delete(cat);
		
	}

	@Override
	public GameModeDto getGameMode(Integer modeId) {
		GameMode cat = this.gameModeRepo.findById(modeId)
				.orElseThrow(() -> new ResourceNotFoundException("GameMode", "mode id", modeId));

		return this.modelMapper.map(cat, GameModeDto.class);
	}

	@Override
	public List<GameModeDto> getGameModes() {

		List<GameMode> mode = this.gameModeRepo.findAll();
		List<GameModeDto> catDtos = mode.stream().map((cat) -> this.modelMapper.map(cat, GameModeDto.class))
				.collect(Collectors.toList());

		return catDtos;
	}

}
