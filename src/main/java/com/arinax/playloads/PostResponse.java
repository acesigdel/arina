package com.arinax.playloads;

import java.util.List;

import lombok.Data;

import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class PostResponse {

	
	private List<PostDto> content;
	private int pageNumber;
	private int pageSize;
	private long totalElements;
	private int totalPages;	
	private boolean lastPage;
	
	
}
