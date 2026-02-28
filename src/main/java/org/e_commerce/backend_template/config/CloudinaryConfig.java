package org.e_commerce.backend_template.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {

  @ConfigurationProperties(prefix = "cloudinary")
  public record CloudinaryProperties(String cloudName, String apiKey, String apiSecret) {
  }

  @Bean
  public Cloudinary cloudinary(final CloudinaryProperties properties) {
    return new Cloudinary(Map.of(
        "cloud_name", properties.cloudName(),
        "api_key", properties.apiKey(),
        "api_secret", properties.apiSecret(),
        "secure", true));
  }
}
