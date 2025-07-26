package com.stock.stock_support.adapter.in.controller.calendar;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.stock_support.adapter.in.controller.calendar.dto.request.GetCalendar;
import com.stock.stock_support.application.port.in.usecase.calendar.GetIpoCalendarUseCase;
import com.stock.stock_support.application.port.in.usecase.calendar.dto.response.IpoCalendarInfo;
import com.stock.stock_support.application.port.in.usecase.indicator.dto.response.ObservationsInfo;
import com.stock.stock_support.global.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GetIpoCalendarController {

	private final GetIpoCalendarUseCase getIpoCalendarUseCase;

	// 경제 데이터 시리즈 지표 조회
	@GetMapping("/calendar/ipo")
	public ResponseEntity<ApiResponse<List<IpoCalendarInfo>>> GetIpoCalendar(
		GetCalendar getCalendar
	) {
		ApiResponse<List<IpoCalendarInfo>> response = ApiResponse.ok(getIpoCalendarUseCase.getIpoCalendar(getCalendar));
		return ApiResponse.toResponseEntity(response);
	}
}
