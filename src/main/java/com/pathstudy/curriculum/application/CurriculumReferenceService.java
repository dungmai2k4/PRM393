package com.pathstudy.curriculum.application;

import com.pathstudy.curriculum.domain.*;
import com.pathstudy.curriculum.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CurriculumReferenceService {
    private final CurriculumRepository curricula;
    private final CurriculumVersionRepository versions;
    private final LearningObjectiveCurriculumRepository objectiveCurricula;
    public CurriculumReferenceService(CurriculumRepository curricula, CurriculumVersionRepository versions, LearningObjectiveCurriculumRepository objectiveCurricula) { this.curricula = curricula; this.versions = versions; this.objectiveCurricula = objectiveCurricula; }
    public List<Curriculum> findBySubjectCode(String subjectCode) { return curricula.findBySubjectCodeOrderByCode(subjectCode); }
    public Optional<CurriculumVersion> findPublishedVersion(String subjectCode) { return versions.findFirstByCurriculumSubjectCodeAndStatusOrderByEffectiveFromDesc(subjectCode, CurriculumVersionStatus.PUBLISHED); }
    public List<LearningObjectiveCurriculum> findObjectivesForCurriculumGrade(Long curriculumGradeId) { return objectiveCurricula.findByCurriculumGradeIdOrderBySequence(curriculumGradeId); }
}
