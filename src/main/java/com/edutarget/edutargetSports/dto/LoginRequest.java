package com.edutarget.edutargetSports.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank
    private String uniqueId;

    @NotBlank
    private String password;
}

