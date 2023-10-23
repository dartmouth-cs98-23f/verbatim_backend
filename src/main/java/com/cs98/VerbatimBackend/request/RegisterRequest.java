package com.cs98.VerbatimBackend.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String password;

}
