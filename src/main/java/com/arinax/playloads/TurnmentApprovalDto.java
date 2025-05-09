package com.arinax.playloads;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnmentApprovalDto {
	
    private Integer postAppId;

private LocalDateTime requestAt;

    private ApprovedStatus status;
    
    public enum ApprovedStatus {
        PENDING,CREATOR_APPROVED, REJECTED
    }
   
    private UserDto user;
  
  //@JsonBackReference
    private PostDto post;
}
