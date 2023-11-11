package com.cs98.VerbatimBackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.Where;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String firstName;

    private String lastName;

    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private String profilePicture;

    private String bio;

    private Integer numGlobalChallengesCompleted;

    private Integer numCustomChallengesCompleted;

    private Integer streak;

    private Boolean hasCompletedDailyChallenge;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlobalChallengeUserResponse> globalChallengeResponses;

    @JsonIgnore
    @OneToMany(mappedBy = "requestingFriend", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRelationship> outboundFriendRequests;

    @JsonIgnore
    @OneToMany(mappedBy = "requestedFriend", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRelationship> inboundFriendRequests;

    public void addOutboundFriendRequest(UserRelationship relationship) {
        this.outboundFriendRequests.add(relationship);
    }

    public void addInboundFriendRequest(UserRelationship relationship) {
        this.outboundFriendRequests.add(relationship);
    }

}
