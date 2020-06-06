package com.wfc.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;
	private HttpStatus status;
	
	public AppException(HttpStatus status, String message) {
		super(message);
		this.status = status;
		this.message = message;
	}
}
