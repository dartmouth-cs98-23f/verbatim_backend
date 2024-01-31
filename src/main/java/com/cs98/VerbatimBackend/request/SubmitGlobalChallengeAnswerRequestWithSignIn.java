package com.cs98.VerbatimBackend.request;

import lombok.Data;

@Data
public class SubmitGlobalChallengeAnswerRequestWithSignIn {

    private String responseQ1;

    private String responseQ2;

    private String responseQ3;

    private Boolean isLogin;

    private String emailOrUsername;

    private String password;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private Boolean withReferral;

    private String referringUsername;


}
