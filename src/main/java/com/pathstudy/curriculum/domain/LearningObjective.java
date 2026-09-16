package com.pathstudy.curriculum.domain;

import com.pathstudy.domain.Subject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "learning_objectives", uniqueConstraints = @UniqueConstraint(name = "uk_objective_subject_code", columnNames = {"subject_id", "code"}), indexes = {@Index(name = "idx_objective_subject", columnList = "subject_id"), @Index(name = "idx_objective_skill", columnList = "primary_skill_id")})
@Getter @Setter @NoArgsConstructor
public class LearningObjective extends AuditedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "subject_id", nullable = false) private Subject subject;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "primary_skill_id", nullable = false) private Skill primarySkill;
    @Column(nullable = false, length = 64) private String code;
    @Column(nullable = false, length = 2000) private String statement;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private CognitiveLevel cognitiveLevel;
    @Column(nullable = false) private boolean active = true;
}
