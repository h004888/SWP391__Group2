package com.OLearning.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Certificate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Certificate {
    /*
     * [Id] BIGINT IDENTITY(1,1) NOT NULL,
     * [UserID] BIGINT NOT NULL,
     * [CourseID] BIGINT NOT NULL,
     * [IssueDate] DATE NULL,
     * [CertificateCode] NVARCHAR(255) NULL,
     * [FileUrl] NVARCHAR(500) NULL,
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UserID")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    // Không cho phép null
    @JoinColumn(name = "CourseID", nullable = false)
    private Course course;

    @Column(name = "IssueDate", columnDefinition = "date", nullable = false)
    private Date issueDate;

    @Column(name = "CertificateCode", columnDefinition = "nvarchar(255)", nullable = false)
    private String certificateCode;

    @Column(name = "FileUrl", columnDefinition = "nvarchar(500)", nullable = false)
    private String fileUrl;

}
