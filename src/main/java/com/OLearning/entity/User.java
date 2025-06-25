package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

import net.minidev.json.annotate.JsonIgnore;
import org.aspectj.weaver.ast.Or;

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
    private Double coin = 0.0;
    private LocalDate birthDay;
    private String address;
    private String profilePicture;
    private String personalSkill;
    private Boolean status;//new

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;

    @OneToMany(mappedBy = "instructor")
    private List<Course> courses;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LessonCompletion> completions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InstructorRequest> instructorRequests;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InstructorRequest> approvedRequests;

//    @OneToMany(mappedBy = "user")
//    private List<CoinTransaction> coinTransactions;


    @OneToMany(mappedBy = "user")
    private List<CoinTransaction> coinTransactions;

    @OneToMany(mappedBy = "instructor")
    private List<Voucher> vouchers;

    @OneToMany(mappedBy = "user")
    private List<UserVoucher> userVouchers;
}