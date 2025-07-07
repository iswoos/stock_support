package com.stock.stock_support.adapter.in.controller.indicator;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.stock_support.adapter.in.controller.indicator.dto.request.GetIndicator;
import com.stock.stock_support.application.port.in.usecase.indicator.GetIndicatorUseCase;
import com.stock.stock_support.application.port.in.usecase.indicator.dto.response.ObservationsInfo;
import com.stock.stock_support.global.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GetIndicatorController {

	private final GetIndicatorUseCase getIndicatorUseCase;

	// 경제 데이터 시리즈 지표 조회
	@GetMapping("/indicator")
	public ResponseEntity<ApiResponse<List<ObservationsInfo>>> GetMarketNews(
		GetIndicator getIndicator
	) {
		ApiResponse<List<ObservationsInfo>> response = ApiResponse.ok(getIndicatorUseCase.getIndicator(getIndicator));
		return ApiResponse.toResponseEntity(response);
	}
}
