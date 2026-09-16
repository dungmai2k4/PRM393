package com.pathstudy.curriculum.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "curriculum_versions", uniqueConstraints = @UniqueConstraint(name = "uk_curriculum_version_code", columnNames = {"curriculum_id", "version_code"}), indexes = {@Index(name = "idx_curriculum_version_curriculum", columnList = "curriculum_id"), @Index(name = "idx_curriculum_version_status", columnList = "status")})
@Getter @Setter @NoArgsConstructor
public class CurriculumVersion extends AuditedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "curriculum_id", nullable = false) private Curriculum curriculum;
    @Column(name = "version_code", nullable = false, length = 64) private String versionCode;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private CurriculumVersionStatus status = CurriculumVersionStatus.DRAFT;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    @Column(length = 2000) private String revisionNote;
}
