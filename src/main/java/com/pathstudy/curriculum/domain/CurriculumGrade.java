package com.pathstudy.curriculum.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "curriculum_grade_mappings", uniqueConstraints = @UniqueConstraint(name = "uk_curriculum_grade", columnNames = {"curriculum_version_id", "grade_id"}), indexes = {@Index(name = "idx_curriculum_grade_version", columnList = "curriculum_version_id"), @Index(name = "idx_curriculum_grade_grade", columnList = "grade_id")})
@Getter @Setter @NoArgsConstructor
public class CurriculumGrade extends AuditedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "curriculum_version_id", nullable = false) private CurriculumVersion curriculumVersion;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "grade_id", nullable = false) private Grade grade;
    @Column(nullable = false) private String title;
    @Column(nullable = false) private int orderIndex;
}
