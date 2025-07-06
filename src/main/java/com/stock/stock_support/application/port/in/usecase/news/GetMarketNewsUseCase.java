package com.stock.stock_support.application.port.in.usecase.news;

import com.stock.stock_support.adapter.in.controller.news.dto.request.GetMarketNews;

public interface GetMarketNewsUseCase {
	String getMarketNews(GetMarketNews getMarketNews);
}
