package com.cs98.VerbatimBackend.request;

import lombok.Data;

@Data
public class FriendAcceptOrDeclineRequest {
    private String requestingUsername;

    private String requestedUsername;

    private Boolean accept;
}
