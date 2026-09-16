package com.pathstudy.curriculum.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "learning_objective_curricula", uniqueConstraints = @UniqueConstraint(name = "uk_objective_curriculum_grade", columnNames = {"learning_objective_id", "curriculum_grade_id"}), indexes = {@Index(name = "idx_objective_curriculum_objective", columnList = "learning_objective_id"), @Index(name = "idx_objective_curriculum_grade", columnList = "curriculum_grade_id")})
@Getter @Setter @NoArgsConstructor
public class LearningObjectiveCurriculum extends AuditedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "learning_objective_id", nullable = false) private LearningObjective learningObjective;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "curriculum_grade_id", nullable = false) private CurriculumGrade curriculumGrade;
    @Column(length = 2000) private String wordingOverride;
    @Column(nullable = false) private int sequence;
    @Column(nullable = false) private boolean required = true;
}
