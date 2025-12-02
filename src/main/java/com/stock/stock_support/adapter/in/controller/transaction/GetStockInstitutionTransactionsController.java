package com.stock.stock_support.adapter.in.controller.transaction;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.stock_support.adapter.in.controller.transaction.dto.GetStockInstitutionTransactions;
import com.stock.stock_support.application.port.in.usecase.transaction.GetInstitutionTransactionsUseCase;
import com.stock.stock_support.application.port.in.usecase.transaction.dto.response.StockInstitutionTransactionsInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GetStockInstitutionTransactionsController {

	private final GetInstitutionTransactionsUseCase getInstitutionTransactionsUseCase;

	@GetMapping("/institution")
	public ResponseEntity<List<StockInstitutionTransactionsInfo>> getInstitutionTransactions(
		GetStockInstitutionTransactions getStockInstitutionTransactions) {
		List<StockInstitutionTransactionsInfo> response = getInstitutionTransactionsUseCase.getInstitutionTransactions(getStockInstitutionTransactions);

		if (response == null) {
			// 서비스에서 null을 반환했을 경우 (API 오류 등)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		return ResponseEntity.ok(response);
	}
}
