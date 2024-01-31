package com.cs98.VerbatimBackend.misc;

public class Status {
    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int USER_NOT_FOUND = 460;
    public static final int USERNAME_TAKEN = 461;
    public static final int EMAIL_TAKEN = 462;
    public static final int WRONG_PASSWORD = 463;
    public static final int OLD_PASSWORD_SAME_AS_NEW_PASSWORD = 464;
    public static final int GROUP_NOT_FOUND = 465;
    public static final int GROUP_CHALLENGE_NOT_FOUND = 466;
    public static final int USER_NOT_IN_GROUP = 467;
    public static final int GROUP_CREATION_FAILED = 468;

    public static final int USER_ALREADY_IN_GROUP = 469;

    public static final int USER_ADDED_TO_GROUP_FAILED = 470;
    public static final int USER_GROUP_REMOVAL_FAILED = 471;

    public static final int FETCH_GROUP_STATS_FAILED = 472;

    public static final int ACTIVE_FRIENDSHIP_NOT_FOUND = 473;

}
