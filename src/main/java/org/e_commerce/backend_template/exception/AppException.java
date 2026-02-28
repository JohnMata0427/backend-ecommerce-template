package org.e_commerce.backend_template.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

	private final HttpStatus status;

	public AppException(final String message, final HttpStatus status) {
		super(message);
		this.status = status;
	}
}
