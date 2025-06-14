package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private Long coin;
    private LocalDate birthday;
    private String address;
    private String profilePicture;
    private String personalSkill;
    private boolean status;//new

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;

    @OneToMany(mappedBy = "instructor")
    private List<Course> courses;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<InstructorRequest> instructorRequests;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<InstructorRequest> approvedRequests;
//    @OneToMany(mappedBy = "user")
//    private List<CoinTransaction> coinTransactions;
}

