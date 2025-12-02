package com.stock.stock_support.adapter.in.controller.transaction;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stock.stock_support.adapter.in.controller.transaction.dto.GetStockInsiderTransactions;
import com.stock.stock_support.application.port.in.usecase.transaction.GetStockInsiderTransactionsUseCase;
import com.stock.stock_support.application.port.in.usecase.transaction.dto.response.StockInsiderTransactionsInfo;
import com.stock.stock_support.global.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GetStockInsiderTransactionsController {

	private final GetStockInsiderTransactionsUseCase getStockInsiderTransactionsUseCase;

	// 내부자 거래 내역 조회
	@GetMapping("/transactions/insider")
	public ResponseEntity<ApiResponse<List<StockInsiderTransactionsInfo>>> GetInsiderTransactions(
		GetStockInsiderTransactions getStockInsiderTransactions
	) {
		ApiResponse<List<StockInsiderTransactionsInfo>> response = ApiResponse.ok(getStockInsiderTransactionsUseCase.getStockInsiderTransactions(getStockInsiderTransactions));
		return ApiResponse.toResponseEntity(response);
	}
}
