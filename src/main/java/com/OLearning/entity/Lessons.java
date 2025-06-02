//package com.OLearning.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Entity
//@Table(name = "Lessons")
//@NoArgsConstructor
//@AllArgsConstructor
//@Getter
//@Setter
//@ToString
//public class Lessons {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long lessonId;
//    private String title;
//    private String description;
//    private String contentType = "video";
//    private Integer orderNumber;
//    private Boolean isFree;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    @ManyToOne
//    @JoinColumn(name = "chapterId")
//    private Chapters chapter;
//
//    @OneToMany(mappedBy="lesson")
//    private List<Video> videos;
//}
