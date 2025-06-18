package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Chapters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chapters {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ChapterID")
  private Long chapterId;

  @Column(name = "Title")
  private String title;

  @Column(name = "Description")
  private String description;

  @Column(name = "OrderNumber")
  private Integer orderNumber;

  @Column(name = "CreatedAt")
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "CourseID")
  private Course course;

  @OneToMany(mappedBy = "chapter", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Lesson> lessons;
}
