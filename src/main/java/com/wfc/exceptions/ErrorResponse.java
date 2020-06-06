package com.wfc.exceptions;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ErrorResponse {

	private String message;
	private LocalDateTime time;
	private String errorCode;
}
