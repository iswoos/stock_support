package com.stock.stock_support.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.stock.stock_support.global.dto.response.ApiResponse;

@RestControllerAdvice
public class ApiControllerAdvice {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Object>> customException(CustomException e) {
		ApiResponse<Object> response = ApiResponse.of(
			e.getHttpStatus(),
			e.getMessage(),
			null
		);
		return ApiResponse.toResponseEntity(response);
	}
}
