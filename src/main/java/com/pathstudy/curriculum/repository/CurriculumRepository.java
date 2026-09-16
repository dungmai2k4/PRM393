package com.pathstudy.curriculum.repository;
import com.pathstudy.curriculum.domain.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> { List<Curriculum> findBySubjectCodeOrderByCode(String subjectCode); }
