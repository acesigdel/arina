package com.arinax.entities;

import java.time.LocalDateTime;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roomId;

    @Lob
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime addedDate;

//    @Column(name = "event_start")
//    private LocalDateTime startTime;

    @Builder.Default
    private double entryFee = 0.0;

    private double wining;
    private String gameType; // e.g. "1v1", "2v2", ..., "8v8"

    @Enumerated(EnumType.STRING)
    private RoomStatus status;
    
    public enum RoomStatus {
        PENDING,PLAYER_APPROVED, DISAPPEAR, PRIVATE
    }

    @Builder.Default
    private int inventory = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
