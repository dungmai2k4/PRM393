package com.pathstudy.assessment;

import com.pathstudy.assessment.application.AssessmentService;
import com.pathstudy.assessment.domain.*;
import com.pathstudy.assessment.repository.*;
import com.pathstudy.curriculum.domain.*;
import com.pathstudy.curriculum.repository.*;
import com.pathstudy.domain.Subject;
import com.pathstudy.domain.User;
import com.pathstudy.repo.SubjectRepository;
import com.pathstudy.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(AssessmentService.class)
class AssessmentCoreTest {
    @Autowired SubjectRepository subjects; @Autowired UserRepository users;
    @Autowired QuestionFamilyRepository families; @Autowired QuestionVersionRepository questionVersions; @Autowired QuestionOptionRepository options;
    @Autowired QuestionObjectiveAlignmentRepository alignments; @Autowired AssessmentDefinitionRepository definitions; @Autowired AssessmentVersionRepository assessmentVersions;
    @Autowired AssessmentItemRepository items; @Autowired AssessmentAttemptRepository attempts; @Autowired AttemptAnswerRepository answers;
    @Autowired SkillRepository skills; @Autowired LearningObjectiveRepository objectives; @Autowired AssessmentService service;

    @Test void stableAndRevisionKeysAreConstrained() {
        Subject subject = subject("uniqueness"); QuestionFamily family = family(subject, "Q-1");
        assertThatThrownBy(() -> family(subject, "Q-1")).isInstanceOf(DataIntegrityViolationException.class);
        question(family, 1);
        assertThatThrownBy(() -> question(family, 1)).isInstanceOf(DataIntegrityViolationException.class);
        AssessmentDefinition definition = definition(subject, "A-1");
        assertThatThrownBy(() -> definition(subject, "A-1")).isInstanceOf(DataIntegrityViolationException.class);
        assessment(definition, 1);
        assertThatThrownBy(() -> assessment(definition, 1)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test void optionsAreOrderedAndOwnedByTheirQuestionVersion() {
        QuestionVersion question = question(family(subject("options"), "Q"), 1);
        option(question, 2, false); option(question, 1, true);
        assertThat(options.findByQuestionVersionIdOrderByOptionIndexAsc(question.getId())).extracting(QuestionOption::getOptionIndex).containsExactly(1, 2);
        assertThatThrownBy(() -> option(question, 1, false)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test void objectiveAlignmentAndAssessmentItemOrderingAreConstrained() {
        Subject subject = subject("alignment"); QuestionVersion question = question(family(subject, "Q"), 1); LearningObjective objective = objective(subject);
        alignment(question, objective);
        assertThatThrownBy(() -> alignment(question, objective)).isInstanceOf(DataIntegrityViolationException.class);
        AssessmentVersion assessment = assessment(definition(subject, "A"), 1); item(assessment, question, 2);
        assertThat(items.findByAssessmentVersionIdOrderBySequenceNoAsc(assessment.getId())).extracting(AssessmentItem::getSequenceNo).containsExactly(2);
        QuestionVersion other = question(family(subject, "Q2"), 1);
        assertThatThrownBy(() -> item(assessment, other, 2)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test void startsAnswersGradesUpdatesAndSubmitsSingleChoiceAttempt() {
        Subject subject = subject("flow"); User user = user(); QuestionVersion question = question(family(subject, "Q"), 1);
        QuestionOption wrong = option(question, 0, false); QuestionOption correct = option(question, 1, true);
        AssessmentVersion assessment = assessment(definition(subject, "A"), 1); AssessmentItem item = item(assessment, question, 1);
        AssessmentAttempt attempt = service.startAttempt(user, assessment);
        assertThat(attempt.getStatus()).isEqualTo(AttemptStatus.IN_PROGRESS); assertThat(attempt.getAttemptNumber()).isEqualTo(1); assertThat(attempt.getStartedAt()).isNotNull();
        AttemptAnswer answer = service.saveSingleChoiceAnswer(attempt, item, correct);
        assertThat(answer.getIsCorrect()).isTrue(); assertThat(answer.getEarnedScore()).isEqualByComparingTo("2.00"); assertThat(answer.getGradingStatus()).isEqualTo(GradingStatus.AUTO_GRADED);
        AttemptAnswer updated = service.saveSingleChoiceAnswer(attempt, item, wrong);
        assertThat(updated.getId()).isEqualTo(answer.getId()); assertThat(answers.count()).isEqualTo(1); assertThat(updated.getEarnedScore()).isEqualByComparingTo(BigDecimal.ZERO);
        service.saveSingleChoiceAnswer(attempt, item, correct);
        AssessmentAttempt submitted = service.submitAttempt(attempt);
        assertThat(submitted.getStatus()).isEqualTo(AttemptStatus.SUBMITTED); assertThat(submitted.getTotalScore()).isEqualByComparingTo("2.00"); assertThat(submitted.getMaxScore()).isEqualByComparingTo("2.00"); assertThat(submitted.getPercentage()).isEqualByComparingTo("100.00");
        assertThatThrownBy(() -> service.saveSingleChoiceAnswer(submitted, item, correct)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> service.submitAttempt(submitted)).isInstanceOf(IllegalStateException.class);
    }

    @Test void rejectsOptionFromAnotherQuestion() {
        Subject subject = subject("foreign-option"); User user = user(); QuestionVersion question = question(family(subject, "Q1"), 1); QuestionVersion other = question(family(subject, "Q2"), 1);
        AssessmentItem item = item(assessment(definition(subject, "A"), 1), question, 1); AssessmentAttempt attempt = service.startAttempt(user, item.getAssessmentVersion());
        assertThatThrownBy(() -> service.saveSingleChoiceAnswer(attempt, item, option(other, 0, true))).isInstanceOf(IllegalArgumentException.class);
    }

    private Subject subject(String code) { Subject s = new Subject(); s.setCode(code); s.setName(code); return subjects.saveAndFlush(s); }
    private User user() { User u = new User(); u.setEmail("user" + System.nanoTime() + "@example.com"); u.setFullName("User"); u.setPasswordHash("hash"); return users.saveAndFlush(u); }
    private QuestionFamily family(Subject s, String code) { QuestionFamily f = new QuestionFamily(); f.setSubject(s); f.setStableCode(code); f.setInternalName(code); return families.saveAndFlush(f); }
    private QuestionVersion question(QuestionFamily family, int revision) { QuestionVersion q = new QuestionVersion(); q.setQuestionFamily(family); q.setRevisionNo(revision); q.setStatus(VersionStatus.PUBLISHED); q.setQuestionType(QuestionType.SINGLE_CHOICE); q.setPrompt("Question"); q.setPublishedAt(LocalDateTime.now()); return questionVersions.saveAndFlush(q); }
    private QuestionOption option(QuestionVersion question, int index, boolean correct) { QuestionOption o = new QuestionOption(); o.setQuestionVersion(question); o.setOptionIndex(index); o.setContent("Option " + index); o.setCorrect(correct); return options.saveAndFlush(o); }
    private AssessmentDefinition definition(Subject s, String code) { AssessmentDefinition d = new AssessmentDefinition(); d.setSubject(s); d.setStableCode(code); d.setName(code); d.setType(AssessmentType.DIAGNOSTIC); return definitions.saveAndFlush(d); }
    private AssessmentVersion assessment(AssessmentDefinition d, int revision) { AssessmentVersion a = new AssessmentVersion(); a.setAssessmentDefinition(d); a.setRevisionNo(revision); a.setStatus(VersionStatus.PUBLISHED); a.setPublishedAt(LocalDateTime.now()); return assessmentVersions.saveAndFlush(a); }
    private AssessmentItem item(AssessmentVersion assessment, QuestionVersion question, int sequence) { AssessmentItem i = new AssessmentItem(); i.setAssessmentVersion(assessment); i.setQuestionVersion(question); i.setSequenceNo(sequence); i.setPoints(new BigDecimal("2.00")); i.setRequired(true); return items.saveAndFlush(i); }
    private LearningObjective objective(Subject subject) { Skill skill = new Skill(); skill.setSubject(subject); skill.setCode("READ"); skill.setName("Read"); skill = skills.saveAndFlush(skill); LearningObjective o = new LearningObjective(); o.setSubject(subject); o.setPrimarySkill(skill); o.setCode("OBJ"); o.setStatement("Objective"); o.setCognitiveLevel(CognitiveLevel.UNDERSTAND); return objectives.saveAndFlush(o); }
    private QuestionObjectiveAlignment alignment(QuestionVersion q, LearningObjective objective) { QuestionObjectiveAlignment a = new QuestionObjectiveAlignment(); a.setQuestionVersion(q); a.setLearningObjective(objective); return alignments.saveAndFlush(a); }
}
