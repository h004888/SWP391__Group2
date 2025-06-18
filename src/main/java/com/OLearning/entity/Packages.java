package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Packages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;
    private String packageName;
    private Long price;
    private Integer coursesCreated;
    private Integer duration;
    private Boolean isActive;
    @OneToMany(mappedBy = "packages", fetch = FetchType.LAZY)
    private List<BuyPackages> listOfBuyPackage;
}
