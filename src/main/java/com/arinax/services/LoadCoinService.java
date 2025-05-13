package com.arinax.services;

import com.arinax.playloads.LoadCoinDto;


public interface LoadCoinService {

	// create
		LoadCoinDto createLoadCoin(LoadCoinDto loadCoinDto, Integer userId);

		// update
		LoadCoinDto updateLoadCoin(LoadCoinDto loadCoinDto, Integer loadCoinId);

			
		LoadCoinDto approvedLoadCoin(LoadCoinDto loadCoinDto, Integer loadCoinId);
		
		LoadCoinDto rejectLoadCoin(LoadCoinDto loadCoinDto, Integer loadCoinId);
}
