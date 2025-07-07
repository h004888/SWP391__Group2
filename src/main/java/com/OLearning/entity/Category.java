package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
@Setter
@Table(name = "Categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryID")
    private Long id;
    
    @Column(name = "Name", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)", unique = true)
    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Course> courses ;
}