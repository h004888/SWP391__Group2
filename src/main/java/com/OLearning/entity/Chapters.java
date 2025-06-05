package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chapters {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long chapterId;

  private String title;

  private String description;

  private Integer orderNumber;

  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "CourseID")
  private Course course;

  @OneToMany(mappedBy = "chapter")
  private List<Lesson> lessons;

}
