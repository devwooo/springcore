package hello.core;

import hello.core.config.AutoAppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@SpringBootTest(classes = AutoAppConfig.class)
class CoreApplicationTests {

	@Test
	void contextLoads() {

	}

}
