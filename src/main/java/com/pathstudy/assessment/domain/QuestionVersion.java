package com.pathstudy.assessment.domain;
import com.pathstudy.curriculum.domain.*; import jakarta.persistence.*; import lombok.*; import java.time.LocalDateTime;
@Entity @Table(name="question_versions", uniqueConstraints=@UniqueConstraint(name="uk_question_version_family_revision", columnNames={"question_family_id","revision_no"}), indexes={@Index(name="idx_question_version_family",columnList="question_family_id"),@Index(name="idx_question_version_curriculum",columnList="curriculum_version_id")})
@Getter @Setter @NoArgsConstructor public class QuestionVersion extends AuditedEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="question_family_id",nullable=false) private QuestionFamily questionFamily;
 @Column(name="revision_no",nullable=false) private int revisionNo;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private VersionStatus status=VersionStatus.DRAFT;
 @Enumerated(EnumType.STRING) @Column(name="question_type",nullable=false,length=24) private QuestionType questionType;
 @Column(nullable=false,length=4000) private String prompt; @Column(length=4000) private String explanation; private Integer difficulty;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="curriculum_version_id") private CurriculumVersion curriculumVersion;
 private LocalDateTime publishedAt;
}
