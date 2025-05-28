package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Packages")
public class Packages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PackagesId")
    private int packagesId;
    @Column(name = "PackagesName")
    private String packagesName;
    @Column(name = "Price")
    private double price;
    @Column(name = "CoursesCreated")
    private int coursesCreated;
    @Column(name = "Duration")
    private int duration;

    @OneToMany(mappedBy = "packages", cascade = CascadeType.ALL)
    private List<BuyPackages> buyPackages;

}
