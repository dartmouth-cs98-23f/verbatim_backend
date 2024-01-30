package com.cs98.VerbatimBackend.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class GroupInviteRequest {

    private int groupId;
    private String invitedUsername;
}
