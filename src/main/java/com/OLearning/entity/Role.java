package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Roles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "users") // ✅ tránh đệ quy vô hạn
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleID")
    private Long roleId;

    @Column(name = "Name")
    private String name;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<User> users;
}
