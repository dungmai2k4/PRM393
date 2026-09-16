package com.pathstudy.curriculum.repository;
import com.pathstudy.curriculum.domain.LearningObjectiveCurriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LearningObjectiveCurriculumRepository extends JpaRepository<LearningObjectiveCurriculum, Long> { List<LearningObjectiveCurriculum> findByCurriculumGradeIdOrderBySequence(Long curriculumGradeId); }
