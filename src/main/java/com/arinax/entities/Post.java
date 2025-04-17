package com.arinax.entities;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@Column(name="post_id, nullable=false")
	private Integer postId;

	@Column(name = "post_title", length = 100, nullable = false)
	private String title;

	@Column(length = 1000000000)
	private String content;

	private String imageName;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime addedDate;

	@Column(name = "event_start", nullable = true)
	private LocalDateTime startTime;
	
	private double entryFee;
	
	@ManyToOne
	@JoinColumn(name = "game_id")
	private Game game;

	    @ManyToOne
	    @JoinColumn(name = "mode_id", nullable = false)
	    private GameMode gameMode;
	 
	@ManyToOne
	private User user;
	
	
	 

}
