package com.prepvector.backend.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String password;
}