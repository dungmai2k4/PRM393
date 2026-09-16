package com.pathstudy.curriculum.repository;
import com.pathstudy.curriculum.domain.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SkillRepository extends JpaRepository<Skill, Long> { }
