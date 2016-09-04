package org.andrey.testproj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestprojApplication {

	public static void main(String[] args) {
		// System.exit is common for Batch applications since the exit code can be used to
		// drive a workflow
		System.exit(SpringApplication
				.exit(SpringApplication.run(TestprojApplication.class, args)));
	}
}
