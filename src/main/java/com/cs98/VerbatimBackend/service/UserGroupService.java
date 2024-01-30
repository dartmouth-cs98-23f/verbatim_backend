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

    public static int MAX_RAW_GROUP_RATING = 10000;
    public static int MAX_SCALED_GROUP_RATING = 1000;
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

    @Autowired
    private StandardChallengeUserResponseRepository standardChallengeUserResponseRepository;

    @Autowired
    private CustomChallengeUserResponseRepository customChallengeUserResponseRepository;

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
        List<GroupChallenge> challenges = groupChallengeRepository.findByGroupAndIsActiveTrue(group);

        return GetActiveChallengesResponse.builder()
                .activeChallenges(challenges)
                .build();
    }

    public GroupStats getStats(int groupId) {
        UserGroup group = userGroupRepository.findById(groupId);
        List<UserGroupJunction> groupMembers = userGroupJunctionRepository.findByGroupId(groupId);
        List<String> usernames = new ArrayList<>();
        int globalChallsCompleted = 0;
        int groupChallsCompleted = 0;
        int totalStreak = 0;
        int totalGroupVerbatims;
        int totalGlobalVerbatims;

        List<Integer> userIds = new ArrayList<>();
        for (UserGroupJunction member : groupMembers) {
            User user = member.getUser();
            userIds.add(user.getId());
            usernames.add(user.getUsername());
            globalChallsCompleted += user.getNumGlobalChallengesCompleted();
            groupChallsCompleted += user.getNumCustomChallengesCompleted();
            totalStreak += user.getStreak();
        }

        totalStreak = totalStreak > 0 ? totalStreak : 1;

        List<GlobalChallengeUserResponse> completedGlobalChalls = globalChallengeUserResponseRepository.findByUserIdIn(userIds);
        totalGlobalVerbatims = getTotalGlobalVerbatims(completedGlobalChalls);

        List<GroupChallenge> groupChalls = groupChallengeRepository.findByGroup(group);

        List<Integer> standardChallengeIds = new ArrayList<>();
        List<Integer> customChallengeIds = new ArrayList<>();

        for (GroupChallenge chall : groupChalls) {
            if (chall.getIsCustom()) {
                customChallengeIds.add(chall.getId());
            }
            else {
                standardChallengeIds.add(chall.getId());
            }
        }

        List<StandardChallengeResponse> completedStandardChalls = standardChallengeUserResponseRepository.findByChallengeIdIn(standardChallengeIds);
        List<CustomChallengeResponse> completedCustomChalls = customChallengeUserResponseRepository.findByGroupChallengeIdIn(customChallengeIds);

        totalGroupVerbatims = getTotalStandardVerbatims(completedStandardChalls) + getTotalCustomVerbatims(completedCustomChalls);


        double groupScore = (totalStreak * (totalGlobalVerbatims + totalGroupVerbatims)) + groupChallsCompleted + globalChallsCompleted;
        double scaledRating = (MAX_SCALED_GROUP_RATING * groupScore) / MAX_RAW_GROUP_RATING;
        scaledRating = (int)Math.min(scaledRating, MAX_SCALED_GROUP_RATING);

        // groupscore / max_raw = x / max_scaled
        // maxscaled * (groupscore / max_raw)
        List<User> verbaMatch = findVerbaMatch(completedStandardChalls, completedCustomChalls);
        return GroupStats.builder()
                .groupRating(scaledRating)
                .groupMembers(usernames)
                .verbaMatch(verbaMatch)
                .build();

    }

    private int getTotalGlobalVerbatims(List<GlobalChallengeUserResponse> completedChalls) {
        int totalVerbatims = 0;
        Map<Integer, Map<Integer, Set<String>>> verbaMap = new HashMap<>();
        for (GlobalChallengeUserResponse chall : completedChalls) {
            if (verbaMap.containsKey(chall.getId())) {
                if (verbaMap.get(chall.getGlobalChallenge().getId()).get(1).contains(chall.getResponseQ1())) {
                    totalVerbatims += 1;
                }
                if (verbaMap.get(chall.getGlobalChallenge().getId()).get(2).contains(chall.getResponseQ2())) {
                    totalVerbatims += 1;
                }
                if (verbaMap.get(chall.getGlobalChallenge().getId()).get(3).contains(chall.getResponseQ3())) {
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

                verbaMap.put(chall.getGlobalChallenge().getId(), questionToResponses);
            }
        }
        return totalVerbatims;
    }

    private int getTotalStandardVerbatims(List<StandardChallengeResponse> completedChalls) {
        int totalVerbatims = 0;
        Map<Integer, Map<Integer, Set<String>>> verbaMap = new HashMap<>();
        for (StandardChallengeResponse chall : completedChalls) {
            if (verbaMap.containsKey(chall.getChallenge().getId())) {
                if (verbaMap.get(chall.getChallenge().getId()).get(1).contains(chall.getResponseQ1())) {
                    totalVerbatims += 1;
                }
                else {
                    verbaMap.get(chall.getChallenge().getId()).get(1).add(chall.getResponseQ1());
                }
                if (verbaMap.get(chall.getChallenge().getId()).get(2).contains(chall.getResponseQ2())) {
                    totalVerbatims += 1;
                }
                else {
                    verbaMap.get(chall.getChallenge().getId()).get(2).add(chall.getResponseQ2());
                }
                if (verbaMap.get(chall.getChallenge().getId()).get(3).contains(chall.getResponseQ3())) {
                    totalVerbatims += 1;
                }
                else {
                    verbaMap.get(chall.getChallenge().getId()).get(3).add(chall.getResponseQ3());
                }
                if (verbaMap.get(chall.getChallenge().getId()).get(4).contains(chall.getResponseQ4())) {
                    totalVerbatims += 1;
                }
                else {
                    verbaMap.get(chall.getChallenge().getId()).get(4).add(chall.getResponseQ4());
                }
                if (verbaMap.get(chall.getChallenge().getId()).get(5).contains(chall.getResponseQ5())) {
                    totalVerbatims += 1;
                }
                else {
                    verbaMap.get(chall.getChallenge().getId()).get(5).add(chall.getResponseQ5());
                }
            }
            else {
                Set<String> q1Responses = new HashSet<>();
                q1Responses.add(chall.getResponseQ1());

                Set<String> q2Responses = new HashSet<>();
                q2Responses.add(chall.getResponseQ2());

                Set<String> q3Responses = new HashSet<>();
                q3Responses.add(chall.getResponseQ3());

                Set<String> q4Responses = new HashSet<>();
                q4Responses.add(chall.getResponseQ4());

                Set<String> q5Responses = new HashSet<>();
                q5Responses.add(chall.getResponseQ5());

                Map<Integer, Set<String>> questionToResponses = new HashMap<>();
                questionToResponses.put(1, q1Responses);
                questionToResponses.put(2, q2Responses);
                questionToResponses.put(3, q3Responses);
                questionToResponses.put(4, q4Responses);
                questionToResponses.put(5, q5Responses);

                verbaMap.put(chall.getChallenge().getId(), questionToResponses);
            }
        }
        return totalVerbatims;
    }

    private int getTotalCustomVerbatims(List<CustomChallengeResponse> completedChalls) {
        int totalVerbatims = 0;
        Map<Integer, Map<Integer, Set<String>>> verbaMap = new HashMap<>();

        for (CustomChallengeResponse chall : completedChalls) {
            if (verbaMap.containsKey(chall.getChallenge().getId())) {
                if (verbaMap.get(chall.getChallenge().getId()).containsKey(chall.getQuestion().getId())) {
                    if (verbaMap.get(chall.getChallenge().getId()).get(chall.getQuestion().getId()).contains(chall.getResponse())) {
                        totalVerbatims += 1;
                    }
                    else {
                        verbaMap.get(chall.getChallenge().getId()).get(chall.getQuestion().getId()).add(chall.getResponse());
                    }
                }
                else {
                    Set<String> questionResponses = new HashSet<>();
                    Map<Integer, Set<String>> qIdToResponses = new HashMap<>();

                    questionResponses.add(chall.getResponse());
                    verbaMap.get(chall.getChallenge().getId()).put(chall.getQuestion().getId(), questionResponses);
                }

            }
            else {
                Set<String> questionResponses = new HashSet<>();
                Map<Integer, Set<String>> qIdToResponses = new HashMap<>();

                questionResponses.add(chall.getResponse());
                qIdToResponses.put(chall.getQuestion().getId(), questionResponses);

                verbaMap.put(chall.getChallenge().getId(), qIdToResponses);
            }
        }
        return totalVerbatims;
    }

    private List<User> findVerbaMatch(List<StandardChallengeResponse> completedStandards, List<CustomChallengeResponse> completedCustoms) {
        Map<Integer, Map<Integer, Integer>> matchMap = new HashMap<>();
        for (StandardChallengeResponse baseResponse : completedStandards) {
            Integer baseId = baseResponse.getUser().getId();
            if (!matchMap.containsKey(baseId)) {
                Map<Integer, Integer> innerMap = new HashMap<>();
                matchMap.put(baseId, innerMap);
            }

            for (StandardChallengeResponse otherResponse : completedStandards) {
                Integer otherId = otherResponse.getUser().getId();
                if (Objects.equals(baseId, otherId)) {
                    continue;
                }
                if (!Objects.equals(baseResponse.getChallenge().getId(), otherResponse.getChallenge().getId())) {
                    continue;
                }
                if (!matchMap.get(baseId).containsKey(otherId)) {
                    matchMap.get(baseId).put(otherId, 0);
                }
                if (baseResponse.getResponseQ1().equals(otherResponse.getResponseQ1())) {
                    matchMap.get(baseId).put(otherId, matchMap.get(baseId).get(otherId) + 1);
                }
                if (baseResponse.getResponseQ2().equals(otherResponse.getResponseQ2())) {
                    matchMap.get(baseId).put(otherId, matchMap.get(baseId).get(otherId) + 1);
                }
                if (baseResponse.getResponseQ3().equals(otherResponse.getResponseQ3())) {
                    matchMap.get(baseId).put(otherId, matchMap.get(baseId).get(otherId) + 1);
                }
                if (baseResponse.getResponseQ4().equals(otherResponse.getResponseQ4())) {
                    matchMap.get(baseId).put(otherId, matchMap.get(baseId).get(otherId) + 1);
                }
                if (baseResponse.getResponseQ5().equals(otherResponse.getResponseQ5())) {
                    matchMap.get(baseId).put(otherId, matchMap.get(baseId).get(otherId) + 1);
                }
            }

        }

        for (CustomChallengeResponse baseResponse : completedCustoms) {
            Integer baseId = baseResponse.getUser().getId();
            if (!matchMap.containsKey(baseId)) {
                Map<Integer, Integer> innerMap = new HashMap<>();
                matchMap.put(baseId, innerMap);
            }

            for (CustomChallengeResponse otherResponse : completedCustoms) {
                Integer otherId = otherResponse.getUser().getId();
                if (Objects.equals(baseId, otherId)) {
                    continue;
                }
                if (!Objects.equals(baseResponse.getQuestion().getId(), otherResponse.getQuestion().getId())) {
                    continue;
                }
                if (!matchMap.get(baseId).containsKey(otherId)) {
                    matchMap.get(baseId).put(otherId, 0);
                }

                if (Objects.equals(baseResponse.getResponse(), otherResponse.getResponse())) {
                    matchMap.get(baseId).put(otherId, matchMap.get(baseId).get(otherId) + 1);
                }
            }
        }
        Integer verbaMatchFirstUser = -1;
        Integer verbaMatchSecondUser = -1;

        int maxVerbatims = 0;
        for (Integer baseUserId : matchMap.keySet()) {
            for (Integer otherUserId : matchMap.get(baseUserId).keySet()) {
                if (matchMap.get(baseUserId).get(otherUserId) > maxVerbatims) {
                    verbaMatchFirstUser = baseUserId;
                    verbaMatchSecondUser = otherUserId;
                    maxVerbatims = matchMap.get(baseUserId).get(otherUserId);
                }
            }
        }
        List<User> verbaMatch = new ArrayList<>();
        if (verbaMatchFirstUser != -1 && verbaMatchSecondUser != -1) {
            Optional<User> user1 = userRepository.findById(verbaMatchFirstUser);
            Optional<User> user2 = userRepository.findById(verbaMatchSecondUser);

            if (user1.isPresent() && user2.isPresent()) {
                verbaMatch.add(user1.get());
                verbaMatch.add(user2.get());
            }
        }
        return verbaMatch;
    }
}
