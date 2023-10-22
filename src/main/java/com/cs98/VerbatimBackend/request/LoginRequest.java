package com.cs98.VerbatimBackend.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String emailOrUsername;
    private String password;
}
