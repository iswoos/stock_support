package com.stock.stock_support.application.port.in.usecase.news;

import java.util.List;

import com.stock.stock_support.adapter.in.controller.news.dto.request.GetMarketNews;
import com.stock.stock_support.application.port.in.usecase.news.dto.response.MarketNewsInfo;

public interface GetMarketNewsUseCase {
	List<MarketNewsInfo> getMarketNews(GetMarketNews getMarketNews);
}
