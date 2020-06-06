package com.wfc.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(AppException.class)
	public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
		return new ResponseEntity<>(ErrorResponse
				.builder()
				.errorCode(ex.getStatus().toString())
				.message(ex.getMessage())
				.time(LocalDateTime.now())
				.build(), ex.getStatus());
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleAppException(Exception ex) {
		return ErrorResponse
				.builder()
				.errorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString())
				.message(ex.getMessage())
				.time(LocalDateTime.now())
				.build();
	}
	
}
