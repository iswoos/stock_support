package com.stock.stock_support.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// 400 Bad Request 요청데이터 옳지않음
	BAD_REQUEST_PARAMETER(400, HttpStatus.BAD_REQUEST, "옳지않은 파라미터입니다."),

	// 404 NOT_FOUND 존재하지 않음

	//500 INTERNAL SERVER ERROR
	INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다.");

	private final int status;
	private final HttpStatus httpStatus;
	private final String message;
}
