package com.wfc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class BootLuncherTests {
	
	@Autowired
	private Environment environment;

	@Test
	void shouldActiveDevEnvWhenApplicationBootStrapped() {
		assertTrue(Arrays.asList(environment.getActiveProfiles()).contains("dev"));
	}

}
