package com.stock.stock_support.adapter.in.controller.news;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.stock.stock_support.adapter.in.controller.news.dto.request.GetCompanyNews;
import com.stock.stock_support.application.port.in.usecase.news.GetCompanyNewsUseCase;
import com.stock.stock_support.global.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GetCompanyNewsController {
	private final GetCompanyNewsUseCase getCompanyNewsUseCase;

	// 최신 회사 뉴스 조회
	@GetMapping("/company-news")
	public ResponseEntity<ApiResponse<String>> GetMarketNews(
		@ModelAttribute GetCompanyNews getCompanyNews
	) {
		ApiResponse<String> response = ApiResponse.ok(getCompanyNewsUseCase.getCompanyNews(getCompanyNews));
		return ApiResponse.toResponseEntity(response);
	}
}
