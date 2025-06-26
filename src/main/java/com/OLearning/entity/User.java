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
    @Column(name = "UserID")
    private Long userId;
    
    @Column(name = "Username")
    private String username;
    
    @Column(name = "Email")
    private String email;
    
    @Column(name = "Password")
    private String password;
    
    @Column(name = "FullName")
    private String fullName;
    
    @Column(name = "Phone")
    private String phone;
    
    @Column(name = "Coin")
    private Double coin = 0.0;
    
    @Column(name = "Birthday")
    private LocalDate birthDay;
    
    @Column(name = "Address")
    private String address;
    
    @Column(name = "ProfilePicture")
    private String profilePicture;
    
    @Column(name = "PersonalSkill")
    private String personalSkill;
    
    @Column(name = "Status")
    private Boolean status;//new

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RoleID")
    private Role role;

    @OneToMany(mappedBy = "instructor", fetch = FetchType.LAZY)
    private List<Course> courses;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InstructorRequest> instructorRequests;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InstructorRequest> approvedRequests;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CoinTransaction> coinTransactions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments;
}

