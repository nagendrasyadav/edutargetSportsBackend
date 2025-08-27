package com.edutarget.edutargetSports.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String uniqueId;
    private String name;
    private String role;
    private String userStatus;
    private String userDisplay;
}
