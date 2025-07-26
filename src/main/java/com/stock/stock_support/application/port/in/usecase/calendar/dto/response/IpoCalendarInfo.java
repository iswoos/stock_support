package com.stock.stock_support.application.port.in.usecase.calendar.dto.response;

public record IpoCalendarInfo(
	String date,
	String exchange,
	String name,
	Long numberOfShares,
	String price,
	String status,
	String symbol,
	Long totalSharesValue
) {
}
