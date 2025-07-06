package com.stock.stock_support.global.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ApiResponse<T>(
	int code,
	HttpStatus httpStatus,
	String message,
	T data
) {
	public static <T> ApiResponse<T> ok(T data) {
		return of(HttpStatus.OK, data);
	}

	public static <T> ApiResponse<T> of(HttpStatus httpStatus, T data) {
		return of(httpStatus, httpStatus.name(), data);
	}

	public static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, T data) {
		return new ApiResponse<>(httpStatus.value(), httpStatus, message, data);
	}

	public static <T> ResponseEntity<ApiResponse<T>> toResponseEntity(ApiResponse<T> apiResponse) {
		return new ResponseEntity(apiResponse, apiResponse.httpStatus);
	}
}