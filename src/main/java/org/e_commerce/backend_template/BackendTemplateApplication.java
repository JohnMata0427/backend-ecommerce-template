package org.e_commerce.backend_template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan
public class BackendTemplateApplication {

	public static void main(final String[] args) {
		SpringApplication.run(BackendTemplateApplication.class, args);
	}

}
