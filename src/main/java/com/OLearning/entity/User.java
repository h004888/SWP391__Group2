package com.OLearning.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Normalized;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Long userId;
    
    @Column(name = "Username", columnDefinition = "nvarchar(255)")
    @Normalized
    private String username;
    
    @Column(name = "Email")
    private String email;
    
    @Column(name = "Password")
    private String password;
    
    @Column(name = "FullName", columnDefinition = "nvarchar(255)")
    private String fullName;
    
    @Column(name = "Phone")
    private String phone;
    
    @Column(name = "Coin")
    private Double coin = 0.0;
    
    @Column(name = "Birthday")
    private LocalDate birthDay;
    
    @Column(name = "Address" , columnDefinition = "nvarchar(255)")
    private String address;
    
    @Column(name = "ProfilePicture")
    private String profilePicture;
    
    @Column(name = "IsGooglePicture")
    private Boolean isGooglePicture = false;
    
    @Column(name = "PersonalSkill" , columnDefinition = "nvarchar(MAX)")
    private String personalSkill;
    
    @Column(name = "Status")
    private Boolean status;//new

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RoleID")
    private Role role;

    @OneToMany(mappedBy = "instructor", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Course> courses;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> order;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<InstructorRequest> instructorRequests;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<InstructorRequest> approvedRequests;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CoinTransaction> coinTransactions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "instructor")
    @JsonIgnore
    private List<Voucher> vouchers;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<UserVoucher> userVouchers;
}


