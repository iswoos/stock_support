package com.stock.stock_support.adapter.in.controller.indicator;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.stock_support.adapter.in.controller.indicator.dto.request.GetIndicatorName;
import com.stock.stock_support.adapter.in.controller.indicator.dto.request.GetIndicatorSeriesId;
import com.stock.stock_support.application.port.in.usecase.indicator.GetIndicatorUseCase;
import com.stock.stock_support.application.port.in.usecase.indicator.dto.response.ObservationsInfo;
import com.stock.stock_support.global.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GetIndicatorController {

	private final GetIndicatorUseCase getIndicatorUseCase;

	// Fred 경제 데이터 시리즈 지표 조회
	@GetMapping("/indicator/series")
	public ResponseEntity<ApiResponse<List<ObservationsInfo>>> GetMarketNewsBySeries(
		GetIndicatorSeriesId getIndicatorSeriesId
	) {
		ApiResponse<List<ObservationsInfo>> response = ApiResponse.ok(getIndicatorUseCase.getIndicatorBySeriesId(getIndicatorSeriesId));
		return ApiResponse.toResponseEntity(response);
	}

	// FMP 경제 데이터 시리즈 지표 조회
	@GetMapping("/indicator/name")
	public ResponseEntity<ApiResponse<List<ObservationsInfo>>> GetMarketNewsByName(
		GetIndicatorName getIndicatorName
	) {
		ApiResponse<List<ObservationsInfo>> response = ApiResponse.ok(getIndicatorUseCase.getIndicatorByName(getIndicatorName));
		return ApiResponse.toResponseEntity(response);
	}
}
