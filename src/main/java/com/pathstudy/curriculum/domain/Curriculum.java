package com.pathstudy.curriculum.domain;

import com.pathstudy.domain.Subject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "curricula", uniqueConstraints = @UniqueConstraint(name = "uk_curriculum_subject_code", columnNames = {"subject_id", "code"}), indexes = @Index(name = "idx_curriculum_subject", columnList = "subject_id"))
@Getter @Setter @NoArgsConstructor
public class Curriculum extends AuditedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "subject_id", nullable = false) private Subject subject;
    @Column(nullable = false, length = 64) private String code;
    @Column(nullable = false) private String name;
    @Column(length = 2000) private String description;
}
