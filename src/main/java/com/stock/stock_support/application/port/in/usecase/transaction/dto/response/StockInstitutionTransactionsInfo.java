package com.stock.stock_support.application.port.in.usecase.transaction.dto.response;

import lombok.Builder;

@Builder
public record StockInstitutionTransactionsInfo(
	String filerName,           // 기관 투자자 이름
	long shares,                // 보유 주식 수
	long change,                // 주식 수 변화량
	String filingDate,          // 신고 날짜
	double marketValue,         // 시장 가치 (달러)
	double percentOfPortfolio,  // 포트폴리오 내 비중 (%)
	String assetType            // 자산 유형 (SHARES, CALL, PUT, PRN 등)
) {
}