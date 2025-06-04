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
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private LocalDate birthday;
    private String address;
    private String profilePicture;

    @OneToMany(mappedBy = "user")
    private List<Enrollment> enrollments;

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;

    @OneToMany(mappedBy = "instructor")
    private List<Course> courses;

    @OneToMany(mappedBy = "instructor", fetch = FetchType.LAZY)
    private List<BuyPackages> listOfBuyPackage;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
    
}
