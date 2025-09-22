package org.una.progra3.healthy_life;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HealthyLifeApplicationTests.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class HealthyLifeApplicationTests {

	@Test
	void contextLoads() {
	}

	@SpringBootConfiguration
	@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
	static class TestConfig {
		// Minimal application for context loading in tests only.
	}
}
