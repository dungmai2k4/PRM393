package com.pathstudy.curriculum.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "curriculum_structure_nodes", uniqueConstraints = @UniqueConstraint(name = "uk_structure_node_version_code", columnNames = {"curriculum_version_id", "code"}), indexes = {@Index(name = "idx_structure_version", columnList = "curriculum_version_id"), @Index(name = "idx_structure_parent", columnList = "parent_id"), @Index(name = "idx_structure_grade", columnList = "curriculum_grade_id")})
@Getter @Setter @NoArgsConstructor
public class CurriculumStructureNode extends AuditedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "curriculum_version_id", nullable = false) private CurriculumVersion curriculumVersion;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "curriculum_grade_id") private CurriculumGrade curriculumGrade;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "parent_id") private CurriculumStructureNode parent;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private CurriculumStructureNodeType type;
    @Column(nullable = false, length = 100) private String code;
    @Column(nullable = false) private String title;
    @Column(length = 2000) private String description;
    @Column(nullable = false) private int orderIndex;

    @PrePersist @PreUpdate
    private void validateParentVersion() {
        if (parent != null && parent.getCurriculumVersion() != curriculumVersion) {
            Long parentVersionId = parent.getCurriculumVersion() == null ? null : parent.getCurriculumVersion().getId();
            Long versionId = curriculumVersion == null ? null : curriculumVersion.getId();
            if (parentVersionId == null || versionId == null || !parentVersionId.equals(versionId)) {
                throw new IllegalArgumentException("A structure node parent must belong to the same curriculum version");
            }
        }
        if (curriculumGrade != null && curriculumGrade.getCurriculumVersion() != curriculumVersion) {
            Long gradeVersionId = curriculumGrade.getCurriculumVersion() == null ? null : curriculumGrade.getCurriculumVersion().getId();
            Long versionId = curriculumVersion == null ? null : curriculumVersion.getId();
            if (gradeVersionId == null || versionId == null || !gradeVersionId.equals(versionId)) {
                throw new IllegalArgumentException("A structure node grade must belong to the same curriculum version");
            }
        }
    }
}
