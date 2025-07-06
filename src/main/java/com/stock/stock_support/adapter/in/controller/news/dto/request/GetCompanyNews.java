package com.stock.stock_support.adapter.in.controller.news.dto.request;

public record GetCompanyNews (
	String symbol,
	String from,
	String to
){}
