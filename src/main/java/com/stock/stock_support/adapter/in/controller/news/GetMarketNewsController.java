package com.stock.stock_support.adapter.in.controller.news;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.stock_support.adapter.in.controller.news.dto.request.GetMarketNews;
import com.stock.stock_support.application.port.in.usecase.news.GetMarketNewsUseCase;
import com.stock.stock_support.global.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GetMarketNewsController {
	private final GetMarketNewsUseCase getMarketNewsUseCase;

	// 최신 뉴스 조회
	@GetMapping("/market-news")
	public ResponseEntity<ApiResponse<String>> GetMarketNews(
		GetMarketNews getMarketNews
	) {
		ApiResponse<String> response = ApiResponse.ok(getMarketNewsUseCase.getMarketNews(getMarketNews));
		return ApiResponse.toResponseEntity(response);
	}
}
