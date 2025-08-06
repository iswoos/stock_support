package com.stock.stock_support.application.port.in.usecase.transaction;

import java.util.List;

import com.stock.stock_support.application.port.in.usecase.transaction.dto.response.MostTransactionsInfo;

public interface GetMostTransactionsUseCase {
	List<MostTransactionsInfo> getMostTransactions();
}
