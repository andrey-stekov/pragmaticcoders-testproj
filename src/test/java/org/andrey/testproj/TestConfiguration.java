package org.andrey.testproj;

import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by andrey on 04.09.2016.
 */
@Configuration
public class TestConfiguration {
    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }
}