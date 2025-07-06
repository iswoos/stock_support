package com.stock.stock_support.adapter.in.controller.transaction;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stock.stock_support.application.port.in.usecase.transaction.GetStockInsiderTransactions;
import com.stock.stock_support.application.port.in.usecase.transaction.dto.response.StockInsiderTransactionsInfo;
import com.stock.stock_support.global.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GetStockInsiderTransactionsController {

	private final GetStockInsiderTransactions getCompanyNewsUseCase;

	// 내부자 거래 내역 조회
	@GetMapping("/transactions/insider")
	public ResponseEntity<ApiResponse<List<StockInsiderTransactionsInfo>>> GetMarketNews(
		com.stock.stock_support.adapter.in.controller.transaction.dto.GetStockInsiderTransactions getStockInsiderTransactions
	) {
		ApiResponse<List<StockInsiderTransactionsInfo>> response = ApiResponse.ok(getCompanyNewsUseCase.getStockInsiderTransactions(getStockInsiderTransactions));
		return ApiResponse.toResponseEntity(response);
	}
}
