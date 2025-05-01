package com.arinax.playloads;

import java.time.LocalDateTime;


import lombok.Data;

@Data
public class RoomApprovalRequestDto {

    private Integer roomAppId;

private LocalDateTime requestAt;


    private ApprovedStatus status;
    
    public enum ApprovedStatus {
        PENDING,CREATOR_APPROVED, REJECTED
    }
  
    private UserDto user;
  

  //@JsonBackReference
    private RoomDto room;

}
