package com.pathstudy.assessment.domain;
import com.pathstudy.curriculum.domain.AuditedEntity; import com.pathstudy.domain.Subject; import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="question_families", uniqueConstraints=@UniqueConstraint(name="uk_question_family_subject_code", columnNames={"subject_id","stable_code"}), indexes=@Index(name="idx_question_family_subject", columnList="subject_id"))
@Getter @Setter @NoArgsConstructor public class QuestionFamily extends AuditedEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="subject_id", nullable=false) private Subject subject;
 @Column(name="stable_code", nullable=false, length=64) private String stableCode;
 @Column(nullable=false, length=255) private String internalName;
}
