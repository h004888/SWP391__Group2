package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
    @Column(name = "Username", unique = true)
    private String username;
    @Column(name = "Email", unique = true)
    private String email;
    @Column(name = "Password")
    private String password;
    @Column(name = "FullName")
    private String fullName;
    @Column(name = "Phone")
    private String phone;
    @Column(name = "Birthday")
    private LocalDate birthday;
    @Column(name = "Address")
    private String address;
    @Column(name = "ProfilePicture")
    private String profilePicture;
    @Column(name = "PersonalSkill")
    private String personalSkill;
    @Column(name = "Coin")
    private Double coin = 0.0;
    @Column(name = "Status")
    private boolean status;

    @OneToMany(mappedBy = "user")
    private List<Enrollment> enrollments;

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;

    @OneToMany(mappedBy = "instructor")
    private List<Course> courses;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;

}
