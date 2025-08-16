package com.stock.stock_support.application.port.in.usecase.transaction;

import java.util.List;

import com.stock.stock_support.adapter.in.controller.transaction.dto.GetStockInstitutionTransactions;
import com.stock.stock_support.application.port.in.usecase.transaction.dto.response.StockInstitutionTransactionsInfo;

public interface GetInstitutionTransactionsUseCase {
	List<StockInstitutionTransactionsInfo> getInstitutionTransactions(GetStockInstitutionTransactions getStockInstitutionTransactions);
}
