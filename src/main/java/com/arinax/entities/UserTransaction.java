package com.arinax.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTransaction {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int transactionId;

	    @ManyToOne
	    @JoinColumn(name = "user_id")
	    private User user;

	    private Double amount; // Positive for credit, Negative for debit
	    private String type;   // "CREDIT" or "DEBIT"
	    private String reason; // e.g. "Ride Deduction", "Top-up"
	    private LocalDateTime dateTime;
}
