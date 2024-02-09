package com.cs98.VerbatimBackend.request;

import lombok.Data;

@Data
public class LeaveGroupRequest {

    private int groupId;
    private String username;
}
