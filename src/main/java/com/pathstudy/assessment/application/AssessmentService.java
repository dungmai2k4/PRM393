package com.pathstudy.assessment.application;

import com.pathstudy.assessment.domain.*;
import com.pathstudy.assessment.repository.*;
import com.pathstudy.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class AssessmentService {
    private final AssessmentAttemptRepository attempts;
    private final AssessmentItemRepository items;
    private final AttemptAnswerRepository answers;

    public AssessmentService(AssessmentAttemptRepository attempts, AssessmentItemRepository items, AttemptAnswerRepository answers) {
        this.attempts = attempts; this.items = items; this.answers = answers;
    }

    @Transactional
    public AssessmentAttempt startAttempt(User user, AssessmentVersion assessmentVersion) {
        AssessmentAttempt attempt = new AssessmentAttempt();
        attempt.setUser(user); attempt.setAssessmentVersion(assessmentVersion);
        attempt.setAttemptNumber((int) attempts.countByUserIdAndAssessmentVersionId(user.getId(), assessmentVersion.getId()) + 1);
        attempt.setStatus(AttemptStatus.IN_PROGRESS); attempt.setStartedAt(LocalDateTime.now());
        return attempts.save(attempt);
    }

    /** Saves the current response for an item; a later save updates the same answer row. */
    @Transactional
    public AttemptAnswer saveSingleChoiceAnswer(AssessmentAttempt attempt, AssessmentItem item, QuestionOption selectedOption) {
        requireInProgress(attempt);
        if (!sameId(attempt.getAssessmentVersion().getId(), item.getAssessmentVersion().getId())) {
            throw new IllegalArgumentException("Assessment item does not belong to this attempt's assessment version");
        }
        if (item.getQuestionVersion().getQuestionType() != QuestionType.SINGLE_CHOICE) {
            throw new IllegalArgumentException("Question type does not support a single-choice response");
        }
        if (!sameId(item.getQuestionVersion().getId(), selectedOption.getQuestionVersion().getId())) {
            throw new IllegalArgumentException("Selected option does not belong to the assessment item's question version");
        }
        AttemptAnswer answer = answers.findByAssessmentAttemptIdAndAssessmentItemId(attempt.getId(), item.getId())
                .orElseGet(AttemptAnswer::new);
        answer.setAssessmentAttempt(attempt); answer.setAssessmentItem(item); answer.setSelectedOption(selectedOption);
        answer.setCorrect(selectedOption.isCorrect());
        answer.setEarnedScore(selectedOption.isCorrect() ? item.getPoints() : BigDecimal.ZERO);
        answer.setGradingStatus(GradingStatus.AUTO_GRADED); answer.setAnsweredAt(LocalDateTime.now());
        return answers.save(answer);
    }

    @Transactional
    public AssessmentAttempt submitAttempt(AssessmentAttempt attempt) {
        requireInProgress(attempt);
        BigDecimal max = items.findByAssessmentVersionIdOrderBySequenceNoAsc(attempt.getAssessmentVersion().getId()).stream()
                .map(AssessmentItem::getPoints).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = answers.findByAssessmentAttemptId(attempt.getId()).stream()
                .map(answer -> answer.getEarnedScore() == null ? BigDecimal.ZERO : answer.getEarnedScore())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        attempt.setMaxScore(max); attempt.setTotalScore(total);
        attempt.setPercentage(max.signum() == 0 ? BigDecimal.ZERO : total.multiply(BigDecimal.valueOf(100)).divide(max, 2, RoundingMode.HALF_UP));
        attempt.setStatus(AttemptStatus.SUBMITTED); attempt.setSubmittedAt(LocalDateTime.now());
        return attempts.save(attempt);
    }

    private void requireInProgress(AssessmentAttempt attempt) {
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) throw new IllegalStateException("Assessment attempt is no longer in progress");
    }
    private boolean sameId(Long first, Long second) { return first != null && first.equals(second); }
}
