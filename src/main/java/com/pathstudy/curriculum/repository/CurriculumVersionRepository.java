package com.pathstudy.curriculum.repository;
import com.pathstudy.curriculum.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CurriculumVersionRepository extends JpaRepository<CurriculumVersion, Long> { Optional<CurriculumVersion> findFirstByCurriculumSubjectCodeAndStatusOrderByEffectiveFromDesc(String subjectCode, CurriculumVersionStatus status); }
