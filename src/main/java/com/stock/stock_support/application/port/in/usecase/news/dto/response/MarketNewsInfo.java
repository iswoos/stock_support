package com.stock.stock_support.application.port.in.usecase.news.dto.response;

public record MarketNewsInfo(
	String category,
	String datetime,
	String headline,
	String id,
	String image,
	String related,
	String source,
	String summary,
	String url
) {
}
