package com.stock.stock_support.adapter.in.controller.news;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.stock_support.adapter.in.controller.news.dto.request.GetMarketNewsCategory;
import com.stock.stock_support.adapter.in.controller.news.dto.request.GetMarketNewsPageLimit;
import com.stock.stock_support.application.port.in.usecase.news.GetMarketNewsUseCase;
import com.stock.stock_support.application.port.in.usecase.news.dto.response.MarketNewsInfo;
import com.stock.stock_support.global.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GetMarketNewsController {
	private final GetMarketNewsUseCase getMarketNewsUseCase;

	// Finhubb 최신 뉴스 조회
	@GetMapping("/news/market-finnhub")
	public ResponseEntity<ApiResponse<List<MarketNewsInfo>>> GetMarketNewsByFinnhub(
		GetMarketNewsCategory getMarketNews
	) {
		ApiResponse<List<MarketNewsInfo>> response = ApiResponse.ok(getMarketNewsUseCase.GetMarketNewsByFinnhub(getMarketNews));
		return ApiResponse.toResponseEntity(response);
	}

	// FMP 최신 뉴스 조회
	@GetMapping("/news/market-fmp")
	public ResponseEntity<ApiResponse<List<MarketNewsInfo>>> GetMarketNewsFmp(
		GetMarketNewsPageLimit getMarketNewsPageLimit
	) {
		ApiResponse<List<MarketNewsInfo>> response = ApiResponse.ok(getMarketNewsUseCase.GetMarketNewsFmp(getMarketNewsPageLimit));
		return ApiResponse.toResponseEntity(response);
	}
}
