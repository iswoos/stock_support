package com.stock.stock_support.application.port.in.usecase.indicator.dto.response;

public record ObservationsInfo(
	String date,     // 한국 시간 문자열
	String value
) {
}
