package com.stock.stock_support.application.port.in.usecase.transaction.dto.response;

public record MostTransactionsInfo(
	String symbol,
	String name,
	double price,
	double change,
	double changesPercentage,
	String exchange
) {
}
