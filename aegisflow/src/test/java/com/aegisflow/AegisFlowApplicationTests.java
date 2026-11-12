package com.aegisflow;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class AegisFlowApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoadsWithoutDatabaseConfiguration() {
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.getBean(AegisFlowApplication.class)).isNotNull();
        assertThat(applicationContext.getEnvironment().getProperty("spring.application.name"))
                .isEqualTo("AegisFlow");
        assertThat(applicationContext.getBeanNamesForType(javax.sql.DataSource.class)).isEmpty();
    }
}

