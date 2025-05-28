package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
@Setter
@Table(name = "Categories")
public class Categories {
    @Column(name = "CategoryID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(name = "Name", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Course> courses ;
}
