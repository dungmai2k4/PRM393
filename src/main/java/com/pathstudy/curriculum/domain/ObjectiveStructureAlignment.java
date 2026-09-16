package com.pathstudy.curriculum.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "objective_structure_alignments", uniqueConstraints = @UniqueConstraint(name = "uk_objective_structure_alignment", columnNames = {"learning_objective_id", "curriculum_structure_node_id", "role"}), indexes = {@Index(name = "idx_alignment_objective", columnList = "learning_objective_id"), @Index(name = "idx_alignment_structure", columnList = "curriculum_structure_node_id")})
@Getter @Setter @NoArgsConstructor
public class ObjectiveStructureAlignment extends AuditedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "learning_objective_id", nullable = false) private LearningObjective learningObjective;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "curriculum_structure_node_id", nullable = false) private CurriculumStructureNode curriculumStructureNode;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private ObjectiveStructureAlignmentRole role;
    private Integer orderIndex;
}
