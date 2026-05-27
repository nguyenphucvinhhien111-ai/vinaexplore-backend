package com.vinatour.backend.dto.request;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String email;
    private String username;
    private String password;
    private String otp; 
}