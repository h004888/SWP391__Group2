package com.OLearning.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

@Entity
@Table(name = "Enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EnrollmentID")
    private int enrollmentID;

    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "CourseID", nullable = false)
    private Course course;

    @Column(name = "EnrollmentDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enrollmentDate;

    @Column(name = "Progress")
    private BigDecimal progress;

    @Column(name = "Status", length = 20)
    private String status;

    @ManyToOne
    @JoinColumn(name = "OrderID")
    private Order order;

}