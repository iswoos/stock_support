package com.stock.stock_support.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CustomException extends RuntimeException {

	private final int status;
	private final HttpStatus httpStatus;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.status = errorCode.getStatus();
		this.httpStatus = errorCode.getHttpStatus();
	}
}
