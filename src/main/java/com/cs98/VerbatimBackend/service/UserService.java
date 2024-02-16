package com.cs98.VerbatimBackend.service;

import com.cs98.VerbatimBackend.misc.Status;
import com.cs98.VerbatimBackend.misc.UserDetailsPrincipal;
import com.cs98.VerbatimBackend.model.User;
import com.cs98.VerbatimBackend.model.UserGroupJunction;
import com.cs98.VerbatimBackend.model.UserRelationship;
import com.cs98.VerbatimBackend.repository.UserGroupJunctionRepository;
import com.cs98.VerbatimBackend.repository.UserRelationshipRepository;
import com.cs98.VerbatimBackend.repository.UserRepository;
import com.cs98.VerbatimBackend.response.GetUserStatsResponse;
import com.cs98.VerbatimBackend.response.GroupStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRelationshipRepository userRelationshipRepository;

    @Autowired
    private UserGroupJunctionRepository userGroupJunctionRepository;

    @Autowired
    private UserGroupService userGroupService;
    @Override
    public UserDetailsPrincipal loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        User userToAuthenticate;
        if (ObjectUtils.isEmpty(emailOrUsername)) {
            return null;
        }

        if (userRepository.existsByEmail(emailOrUsername)) {
            userToAuthenticate = userRepository.findByEmail(emailOrUsername);
        }
        else if (userRepository.existsByUsername(emailOrUsername)) {
            userToAuthenticate = userRepository.findByUsername(emailOrUsername);
        }
        else {
            return null;
        }

        return new UserDetailsPrincipal(userToAuthenticate);

    }

    public GetUserStatsResponse getUserStats(User yourUser, User targetUser) {
        // get the user's stats
        int streak = targetUser.getStreak();
        int groupChal = targetUser.getNumCustomChallengesCompleted();
        int globalChal = targetUser.getNumGlobalChallengesCompleted();

        // get the user's friends
        List<UserRelationship> friends = userRelationshipRepository.findActiveFriendsByUserId(targetUser.getId());
        int numFriends = friends.size();

        // if yourUser and targetUser are friends, get the date, else null
        Date friendsSince = null;
        if (userRelationshipRepository.friendshipExists(yourUser.getId(), targetUser.getId())) {
            UserRelationship frienship = userRelationshipRepository.findFriendship(yourUser.getId(), targetUser.getId());
            friendsSince = frienship.getDate();
        }

        // get the user's highest verbamatch

        // get friend groups
        List<UserGroupJunction> friendGroups = userGroupJunctionRepository.findFriendGroupsByUserId(targetUser.getId());

        // initialize vars
        int highestVerbamatchGroupId = 0;
        double highestVerbamatchScore = -1;

        // loop through all friends
        for (UserGroupJunction group: friendGroups) {
            GroupStats stats = userGroupService.getStats(group.getId());
            double score = stats.getGroupRating();
            if (score > highestVerbamatchScore) {
                highestVerbamatchScore = score;
                highestVerbamatchGroupId = group.getGroup().getId();
            }
        }

        // get the user with the highest score
        User verbamatch = targetUser; // default to self
        List<UserGroupJunction> group = userGroupJunctionRepository.findByGroupId(highestVerbamatchGroupId);
        for (UserGroupJunction person: group) {
            if (!Objects.equals(person.getUser().getId(), targetUser.getId())) {
                verbamatch = person.getUser();
            }
        }

        // create the response
        return GetUserStatsResponse.builder()
                .streak(streak)
                .groupChalllengesCompleted(groupChal)
                .globalChallengesCompleted(globalChal)
                .numFriends(numFriends)
                .verbaMatchUser(verbamatch)
                .verbaMatchScore(highestVerbamatchScore)
                .friendsSince(friendsSince)
                .build();
    }

    public GetUserStatsResponse getUserStats(User user) {
        // get the user's stats
        int streak = user.getStreak();
        int groupChal = user.getNumCustomChallengesCompleted();
        int globalChal = user.getNumGlobalChallengesCompleted();

        // get the user's friends
        List<UserRelationship> friends = userRelationshipRepository.findActiveFriendsByUserId(user.getId());
        int numFriends = friends.size();

        // get the user's highest verbamatch

        // get friend groups
        List<UserGroupJunction> friendGroups = userGroupJunctionRepository.findFriendGroupsByUserId(user.getId());

        // initialize vars
        int highestVerbamatchGroupId = 0;
        double highestVerbamatchScore = -1;

        // loop through all friends
        for (UserGroupJunction group: friendGroups) {
            GroupStats stats = userGroupService.getStats(group.getId());
            double score = stats.getGroupRating();
            if (score > highestVerbamatchScore) {
                highestVerbamatchScore = score;
                highestVerbamatchGroupId = group.getGroup().getId();
            }
        }

        // get the user with the highest score
        User verbamatch = user; // default to self
        List<UserGroupJunction> group = userGroupJunctionRepository.findByGroupId(highestVerbamatchGroupId);
        for (UserGroupJunction person: group) {
            if (!Objects.equals(person.getUser().getId(), user.getId())) {
                verbamatch = person.getUser();
            }
        }

        // create the response
        return GetUserStatsResponse.builder()
                .streak(streak)
                .groupChalllengesCompleted(groupChal)
                .globalChallengesCompleted(globalChal)
                .numFriends(numFriends)
                .verbaMatchUser(verbamatch)
                .verbaMatchScore(highestVerbamatchScore)
                .build();
    }

}
