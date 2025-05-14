package com.arinax.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arinax.entities.UserTransaction;


public interface UserTransactionRepo extends JpaRepository<UserTransaction, Integer>{

	List<UserTransaction> findByUserIdOrderByDateTimeDesc(Integer userId);
}
