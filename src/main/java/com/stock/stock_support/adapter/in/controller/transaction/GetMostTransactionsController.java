package com.stock.stock_support.adapter.in.controller.transaction;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.stock_support.application.port.in.usecase.transaction.GetMostTransactionsUseCase;
import com.stock.stock_support.application.port.in.usecase.transaction.dto.response.MostTransactionsInfo;
import com.stock.stock_support.global.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GetMostTransactionsController {

	private final GetMostTransactionsUseCase getMostTransactionsUseCase;

	// 상위 거래 주식 조회
	@GetMapping("/transactions/most")
	public ResponseEntity<ApiResponse<List<MostTransactionsInfo>>> GetMostTransactions() {
		ApiResponse<List<MostTransactionsInfo>> response = ApiResponse.ok(getMostTransactionsUseCase.getMostTransactions());
		return ApiResponse.toResponseEntity(response);
	}
}
