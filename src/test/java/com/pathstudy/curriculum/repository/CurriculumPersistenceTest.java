package com.pathstudy.curriculum.repository;

import com.pathstudy.curriculum.domain.*;
import com.pathstudy.domain.Subject;
import com.pathstudy.repo.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class CurriculumPersistenceTest {
    @Autowired GradeRepository grades;
    @Autowired CurriculumRepository curricula;
    @Autowired CurriculumVersionRepository versions;
    @Autowired CurriculumGradeRepository curriculumGrades;
    @Autowired CurriculumStructureNodeRepository structureNodes;
    @Autowired SkillRepository skills;
    @Autowired LearningObjectiveRepository objectives;
    @Autowired LearningObjectiveCurriculumRepository objectiveCurricula;
    @Autowired SubjectRepository subjects;

    @Test
    void gradesUseStableCodesAndCanBeReadIdempotently() {
        Grade grade = new Grade(); grade.setCode("THPT_10"); grade.setOrdinal(10); grade.setLabel("Lớp 10");
        grades.saveAndFlush(grade);
        assertThat(grades.findByCode("THPT_10")).containsSame(grade);
        assertThat(grade.getCreatedAt()).isNotNull();
        assertThat(grade.getUpdatedAt()).isNotNull();
    }

    @Test
    void curriculumVersionBelongsToCurriculumAndGradesBelongToVersion() {
        Subject subject = subject("van-version");
        Curriculum curriculum = curriculum(subject, "CORE");
        CurriculumVersion version = version(curriculum, "2026.1");
        Grade grade = grade("THPT_11", 11);
        CurriculumGrade mapping = new CurriculumGrade(); mapping.setCurriculumVersion(version); mapping.setGrade(grade); mapping.setTitle("Lớp 11"); mapping.setOrderIndex(11);
        curriculumGrades.saveAndFlush(mapping);
        assertThat(mapping.getCurriculumVersion().getCurriculum()).isSameAs(curriculum);
        assertThat(mapping.getGrade()).isSameAs(grade);
    }

    @Test
    void skillCodeIsUniqueWithinASubject() {
        Subject subject = subject("van-skill");
        skill(subject, "READ");
        assertThatThrownBy(() -> skill(subject, "READ")).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void learningObjectiveCodeIsUniqueWithinASubject() {
        Subject subject = subject("van-objective"); Skill skill = skill(subject, "READ");
        objective(subject, skill, "MAIN_IDEA");
        assertThatThrownBy(() -> objective(subject, skill, "MAIN_IDEA")).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void objectiveCurriculumMappingIsUnique() {
        Subject subject = subject("van-mapping"); Skill skill = skill(subject, "READ"); LearningObjective objective = objective(subject, skill, "MAIN_IDEA");
        CurriculumGrade curriculumGrade = curriculumGrade(subject, "THPT_12", 12);
        objectiveCurriculum(objective, curriculumGrade);
        assertThatThrownBy(() -> objectiveCurriculum(objective, curriculumGrade)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void structureParentMustUseTheSameVersion() {
        Subject subject = subject("van-tree");
        CurriculumVersion first = version(curriculum(subject, "FIRST"), "1");
        CurriculumVersion second = version(curriculum(subject, "SECOND"), "1");
        CurriculumStructureNode parent = node(first, null, "PARENT");
        CurriculumStructureNode child = node(second, parent, "CHILD");
        assertThatThrownBy(() -> structureNodes.saveAndFlush(child)).isInstanceOf(IllegalArgumentException.class);
    }

    private Subject subject(String code) { Subject subject = new Subject(); subject.setCode(code); subject.setName(code); return subjects.saveAndFlush(subject); }
    private Grade grade(String code, int ordinal) { Grade grade = new Grade(); grade.setCode(code); grade.setOrdinal(ordinal); grade.setLabel(code); return grades.saveAndFlush(grade); }
    private Curriculum curriculum(Subject subject, String code) { Curriculum c = new Curriculum(); c.setSubject(subject); c.setCode(code); c.setName(code); return curricula.saveAndFlush(c); }
    private CurriculumVersion version(Curriculum curriculum, String code) { CurriculumVersion v = new CurriculumVersion(); v.setCurriculum(curriculum); v.setVersionCode(code); v.setStatus(CurriculumVersionStatus.DRAFT); return versions.saveAndFlush(v); }
    private Skill skill(Subject subject, String code) { Skill skill = new Skill(); skill.setSubject(subject); skill.setCode(code); skill.setName(code); return skills.saveAndFlush(skill); }
    private LearningObjective objective(Subject subject, Skill skill, String code) { LearningObjective objective = new LearningObjective(); objective.setSubject(subject); objective.setPrimarySkill(skill); objective.setCode(code); objective.setStatement(code); objective.setCognitiveLevel(CognitiveLevel.UNDERSTAND); return objectives.saveAndFlush(objective); }
    private CurriculumGrade curriculumGrade(Subject subject, String gradeCode, int ordinal) { CurriculumVersion version = version(curriculum(subject, "CORE"), "1"); CurriculumGrade mapping = new CurriculumGrade(); mapping.setCurriculumVersion(version); mapping.setGrade(grade(gradeCode, ordinal)); mapping.setTitle(gradeCode); mapping.setOrderIndex(ordinal); return curriculumGrades.saveAndFlush(mapping); }
    private void objectiveCurriculum(LearningObjective objective, CurriculumGrade grade) { LearningObjectiveCurriculum mapping = new LearningObjectiveCurriculum(); mapping.setLearningObjective(objective); mapping.setCurriculumGrade(grade); mapping.setSequence(1); mapping.setRequired(true); objectiveCurricula.saveAndFlush(mapping); }
    private CurriculumStructureNode node(CurriculumVersion version, CurriculumStructureNode parent, String code) { CurriculumStructureNode node = new CurriculumStructureNode(); node.setCurriculumVersion(version); node.setParent(parent); node.setType(CurriculumStructureNodeType.TOPIC); node.setCode(code); node.setTitle(code); node.setOrderIndex(1); return parent == null ? structureNodes.saveAndFlush(node) : node; }
}
