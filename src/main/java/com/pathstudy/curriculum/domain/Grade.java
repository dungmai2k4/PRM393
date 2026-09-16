package com.pathstudy.curriculum.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "curriculum_grades_reference", indexes = @Index(name = "idx_grade_ordinal", columnList = "ordinal"))
@Getter @Setter @NoArgsConstructor
public class Grade extends AuditedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 32) private String code;
    @Column(nullable = false, unique = true) private int ordinal;
    @Column(nullable = false, length = 100) private String label;
}
