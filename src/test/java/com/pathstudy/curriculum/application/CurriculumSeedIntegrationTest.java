package com.pathstudy.curriculum.application;

import com.pathstudy.config.DataSeeder;
import com.pathstudy.curriculum.repository.GradeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:curriculum-seed;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext
class CurriculumSeedIntegrationTest {
    @Autowired DataSeeder dataSeeder;
    @Autowired GradeRepository grades;

    @Test
    void referenceGradeSeedIsIdempotent() throws Exception {
        assertThat(grades.count()).isEqualTo(3);
        dataSeeder.run();
        assertThat(grades.count()).isEqualTo(3);
        assertThat(grades.findByCode("THPT_10")).isPresent();
        assertThat(grades.findByCode("THPT_11")).isPresent();
        assertThat(grades.findByCode("THPT_12")).isPresent();
    }
}
