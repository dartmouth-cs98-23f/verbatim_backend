package com.cs98.VerbatimBackend.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String username;

    private String oldPassword;

    private String newPassword;
}
