package com.cs98.VerbatimBackend.response;

import com.cs98.VerbatimBackend.model.User;
import lombok.Data;

import java.util.List;

@Data
public class InboundFriendRequestResponse {
    private List<User> friendRequests;
}
