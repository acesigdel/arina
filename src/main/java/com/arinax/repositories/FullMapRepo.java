package com.arinax.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.arinax.entities.FullMap;
import com.arinax.entities.Game;

import com.arinax.entities.User;

public interface FullMapRepo extends JpaRepository<FullMap, Integer> {

	List<FullMap> findByUser(User user);
	List<FullMap> findByGame(Game game);	
	
	@Query("select p from Post p where p.title like :key")
	List<FullMap> searchByTitle(@Param("key") String title);
	Page<FullMap> findByStatus(FullMap.FullMapStatus status, Pageable pageable);

	Page<FullMap> findByStatusNot(FullMap.FullMapStatus status, Pageable pageable);



}
