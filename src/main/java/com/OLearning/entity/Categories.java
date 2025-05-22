package com.OLearning.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private int id;
    @Column(name = "Name", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
    private String name;

}
