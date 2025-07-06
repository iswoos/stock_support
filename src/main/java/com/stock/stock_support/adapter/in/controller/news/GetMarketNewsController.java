package com.stock.stock_support.adapter.in.controller.news;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.stock.stock_support.adapter.in.controller.news.dto.request.GetMarketNews;
import com.stock.stock_support.application.port.in.usecase.news.GetMarketNewsUseCase;
import com.stock.stock_support.application.port.in.usecase.news.dto.response.MarketNewsInfo;
import com.stock.stock_support.global.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GetMarketNewsController {
	private final GetMarketNewsUseCase getMarketNewsUseCase;

	// 최신 뉴스 조회
	@GetMapping("/news/market")
	public ResponseEntity<ApiResponse<List<MarketNewsInfo>>> GetMarketNews(
		GetMarketNews getMarketNews
	) {
		ApiResponse<List<MarketNewsInfo>> response = ApiResponse.ok(getMarketNewsUseCase.getMarketNews(getMarketNews));
		return ApiResponse.toResponseEntity(response);
	}
}
