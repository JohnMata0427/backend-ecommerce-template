package org.e_commerce.backend_template.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    int status,
    String error,
    String message,
    String path,
    Instant timestamp,
    Map<String, String> validationErrors) {

  public ErrorResponse(final int status, final String error, final String message, final String path) {
    this(status, error, message, path, Instant.now(), null);
  }

  public ErrorResponse(final int status, final String error, final String message, final String path,
      final Map<String, String> validationErrors) {
    this(status, error, message, path, Instant.now(), validationErrors);
  }
}
