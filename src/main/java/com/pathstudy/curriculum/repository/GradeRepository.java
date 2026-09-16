package com.pathstudy.curriculum.repository;
import com.pathstudy.curriculum.domain.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface GradeRepository extends JpaRepository<Grade, Long> { Optional<Grade> findByCode(String code); }
