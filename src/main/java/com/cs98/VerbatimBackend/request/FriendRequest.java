package com.cs98.VerbatimBackend.request;

import lombok.Builder;
import lombok.Data;

@Data
public class FriendRequest {
    private String requestingUsername;

    private String requestedUsername;

}
