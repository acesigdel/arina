package com.arinax.playloads;

import java.time.LocalDateTime;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;


@Data
public class FullMapApprovalDto {

    private Integer fullmapAppId;

private LocalDateTime requestAt;


    private ApprovedStatus status;
    
    public enum ApprovedStatus {
        PENDING,CREATOR_APPROVED, REJECTED
    }
   
    private UserDto userId;

    private FullMapDto fullMap;
}
