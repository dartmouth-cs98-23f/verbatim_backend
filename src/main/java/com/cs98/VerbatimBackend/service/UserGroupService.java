package com.cs98.VerbatimBackend.service;

import com.cs98.VerbatimBackend.misc.Status;
import com.cs98.VerbatimBackend.model.*;
import com.cs98.VerbatimBackend.repository.*;
import com.cs98.VerbatimBackend.request.GroupInviteRequest;
import com.cs98.VerbatimBackend.request.LeaveGroupRequest;
import com.cs98.VerbatimBackend.request.UserGroupCreationRequest;
import com.cs98.VerbatimBackend.response.GetActiveChallengesResponse;
import com.cs98.VerbatimBackend.response.GetGroupsResponse;
import com.cs98.VerbatimBackend.response.GroupStats;
import com.cs98.VerbatimBackend.response.UserGroupCreationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.net.ssl.SSLEngineResult;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserGroupService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupJunctionRepository userGroupJunctionRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private GroupChallengeRepository groupChallengeRepository;

    @Autowired
    private GlobalChallengeUserResponseRepository globalChallengeUserResponseRepository;

    public UserGroupCreationResponse createGroup(UserGroupCreationRequest request) {
        User createdByUser = userRepository.findByUsername(request.getCreatedByUsername());

        UserGroup group = UserGroup.builder()
                .name(request.getGroupName())
                .build();

        userGroupRepository.save(group);

        UserGroupJunction createdByGroupMember = UserGroupJunction.builder()
                .group(group)
                .user(createdByUser)
                .build();

        userGroupJunctionRepository.save(createdByGroupMember);

        List<User> usersToAdd = new ArrayList<>();

        if (!ObjectUtils.isEmpty(request.getUsernamesToAdd())) {
            for (String username : request.getUsernamesToAdd()) {
                User userToAdd = userRepository.findByUsername(username);

                if (ObjectUtils.isEmpty(userToAdd)) {
                    return null;
                }
                UserGroupJunction groupMember = UserGroupJunction.builder()
                        .user(userToAdd)
                        .group(group)
                        .build();

                userGroupJunctionRepository.save(groupMember);
                usersToAdd.add(userToAdd);
            }
        }

        return UserGroupCreationResponse.builder()
                .users(usersToAdd)
                .groupName(group.getName())
                .groupId(group.getId())
                .build();
    }

    public GetGroupsResponse findGroupsForUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (ObjectUtils.isEmpty(user)) {
            return null;
        }

        List<UserGroupJunction> userToGroups = userGroupJunctionRepository.findByUserId(user.getId());

        List<UserGroup> groups = new ArrayList<>();
        for (UserGroupJunction userGroup : userToGroups) {
            groups.add(userGroup.getGroup());
        }

        return GetGroupsResponse.builder()
                .groups(groups)
                .build();
    }

    public int inviteUser(GroupInviteRequest request) {
        User invitedUser = userRepository.findByUsername(request.getInvitedUsername());
        if (ObjectUtils.isEmpty(invitedUser)) {
            return Status.USER_NOT_FOUND;
        }

        UserGroup group = userGroupRepository.findById(request.getGroupId());

        if (ObjectUtils.isEmpty(group)) {
            return Status.GROUP_NOT_FOUND;
        }

        if (userGroupJunctionRepository.existsByUserIdAndGroupId(invitedUser.getId(), request.getGroupId())) {
            return Status.USER_ALREADY_IN_GROUP;
        }

        UserGroupJunction userAdded = UserGroupJunction.builder()
                .user(invitedUser)
                .group(group)
                .build();

        userGroupJunctionRepository.save(userAdded);
        return Status.OK;

    }

    public int removeUserFromGroup(LeaveGroupRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        UserGroupJunction userToRemove = userGroupJunctionRepository.findByUserIdAndGroupId(user.getId(), request.getGroupId());
        userGroupJunctionRepository.delete(userToRemove);

        if (!userGroupJunctionRepository.existsByUserIdAndGroupId(user.getId(), request.getGroupId())) {
            return Status.OK;
        }

        return Status.USER_GROUP_REMOVAL_FAILED;
    }

    public GetActiveChallengesResponse getActiveChallenges(int groupId) {
        UserGroup group;
        if (userGroupRepository.existsById(groupId)) {
            group = userGroupRepository.findById(groupId);
        }
        else {
            return null;
        }
        List<GroupChallenge> challenges = groupChallengeRepository.findByGroupAndIsActive(group);

        return GetActiveChallengesResponse.builder()
                .activeChallenges(challenges)
                .build();
    }

    public GroupStats getStats(int groupId) {
        List<UserGroupJunction> groupMembers = userGroupJunctionRepository.findByGroupId(groupId);
        int globalChallsCompleted = 0;
        int groupChallsCompleted = 0;
        int totalStreak = 0;
        int totalGlobalVerbatims;
        int totalGroupVerbatims;

        List<Integer> userIds = new ArrayList<>();
        for (UserGroupJunction member : groupMembers) {
            User user = member.getUser();
            userIds.add(user.getId());
            globalChallsCompleted += user.getNumGlobalChallengesCompleted();
            groupChallsCompleted += user.getNumCustomChallengesCompleted();
            totalStreak += user.getStreak();
        }
        List<GlobalChallengeUserResponse> completedGlobalChalls = globalChallengeUserResponseRepository.findByUserIdIn(userIds);
        totalGlobalVerbatims = getTotalGlobalVerbatims(completedGlobalChalls);

//        List<StandardChallengeResponse> completedStandardChalls = standard
//        totalGroupVerbatims = getTotalStandardVerbatims()
        return GroupStats.builder().build();

    }

    private int getTotalGlobalVerbatims(List<GlobalChallengeUserResponse> completedChalls) {
        int totalVerbatims = 0;
        Map<Integer, Map<Integer, Set<String>>> verbaMap = new HashMap<>();
        for (GlobalChallengeUserResponse chall : completedChalls) {
            if (verbaMap.containsKey(chall.getId())) {
                if (verbaMap.get(chall.getId()).get(1).contains(chall.getResponseQ1())) {
                    totalVerbatims += 1;
                }
                if (verbaMap.get(chall.getId()).get(2).contains(chall.getResponseQ2())) {
                    totalVerbatims += 1;
                }
                if (verbaMap.get(chall.getId()).get(3).contains(chall.getResponseQ3())) {
                    totalVerbatims += 1;
                }
            }
            else {
                Set<String> q1Responses = new HashSet<>();
                q1Responses.add(chall.getResponseQ1());

                Set<String> q2Responses = new HashSet<>();
                q2Responses.add(chall.getResponseQ2());

                Set<String> q3Responses = new HashSet<>();
                q3Responses.add(chall.getResponseQ3());

                Map<Integer, Set<String>> questionToResponses = new HashMap<>();
                questionToResponses.put(1, q1Responses);
                questionToResponses.put(2, q2Responses);
                questionToResponses.put(3, q3Responses);

                verbaMap.put(chall.getId(), questionToResponses);
            }
        }
        return totalVerbatims;
    }
}
