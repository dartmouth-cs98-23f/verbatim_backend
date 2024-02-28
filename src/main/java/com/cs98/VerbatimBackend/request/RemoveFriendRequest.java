package com.cs98.VerbatimBackend.request;

import lombok.Data;

@Data
public class RemoveFriendRequest {

    private String baseUsername;
    private String usernameToRemove;

}
