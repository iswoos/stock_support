package com.stock.stock_support.application.port.in.usecase.transaction;

import java.util.List;

import com.stock.stock_support.application.port.in.usecase.transaction.dto.response.StockInsiderTransactionsInfo;

public interface GetStockInsiderTransactionsUseCase {
	List<StockInsiderTransactionsInfo> getStockInsiderTransactions(
		com.stock.stock_support.adapter.in.controller.transaction.dto.GetStockInsiderTransactions getStockInsiderTransactions);
}
