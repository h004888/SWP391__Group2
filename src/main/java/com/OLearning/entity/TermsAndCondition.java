package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "TermsAndConditions")
public class TermsAndCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Vai trò áp dụng: USER, INSTRUCTOR, hoặc ALL
    @Column(name="RoleTarget", nullable = false, length = 20)
    private String roleTarget;

    // Tiêu đề điều khoản
    @Column(name = "SectionTitle", nullable = false, length = 255 , columnDefinition = "nvarchar(255)")
    private String sectionTitle;

    // Nội dung chi tiết điều khoản
    @Lob
    @Column(name="Content",columnDefinition = "nvarchar(max)", nullable = false)
    private String content;

    // Thứ tự hiển thị
    @Column(name = "DisplayOrder")
    private Integer displayOrder;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

}
