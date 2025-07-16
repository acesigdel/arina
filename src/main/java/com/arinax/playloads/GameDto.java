package com.arinax.playloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GameDto {

	private Integer gameId;
	
	private String gameTitle;

	private String gameDescription;

}