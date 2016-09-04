package org.andrey.testproj;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BatchConfiguration.class})
@ContextConfiguration(classes = {TestConfiguration.class})
@EnableAutoConfiguration
public class TestprojApplicationTests {
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	public void contextLoads() throws Exception {
		ExitStatus jobExecution = jobLauncherTestUtils.launchJob().getExitStatus();
		assertThat(jobExecution.getExitCode())
			.isEqualTo("COMPLETED");
	}
}
