package com.stock.stock_support.application.port.in.usecase.transaction.dto.response;

public record StockInsiderTransactionsInfo(
	Long change,
	String currency,
	String filingDate,
	String id,
	Boolean isDerivative,
	String name,
	Long share,
	String source,
	String symbol,
	String transactionCode,
	String transactionDate,
	Double transactionPrice
)
{ }