package com.stock.stock_support.application.service.transaction;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.stock_support.application.port.in.usecase.news.dto.response.CompanyNewsInfo;
import com.stock.stock_support.application.port.in.usecase.transaction.GetStockInsiderTransactions;
import com.stock.stock_support.application.port.in.usecase.transaction.dto.response.StockInsiderTransactionsInfo;
import com.stock.stock_support.global.common.Timezone;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetStockInsiderTransactionsService implements GetStockInsiderTransactions {

	@Value("${finnhub.api-key}")
	private String api_key;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Timezone timezone;

	@Override
	public List<StockInsiderTransactionsInfo> getStockInsiderTransactions(
		com.stock.stock_support.adapter.in.controller.transaction.dto.GetStockInsiderTransactions getStockInsiderTransactions) {
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(new URI("https://finnhub.io/api/v1/stock/insider-transactions?symbol=%s&token=%s".formatted(
					getStockInsiderTransactions.symbol(), api_key)))
				.GET()
				.build();

			HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			// Jackson으로 JSON 파싱
			JsonNode transactions = objectMapper.readTree(response.body()).path("data");

			List<StockInsiderTransactionsInfo> result = new ArrayList<>();
			for (JsonNode transaction : transactions) {
				result.add(new StockInsiderTransactionsInfo(
					transaction.path("change").asLong(),
					transaction.path("currency").asText(),
					timezone.convertUsDateToKoreanDate(transaction.path("filingDate").asText()),
					transaction.path("id").asText(),
					transaction.path("isDerivative").asBoolean(),
					transaction.path("name").asText(),
					transaction.path("share").asLong(),
					transaction.path("source").asText(),
					transaction.path("symbol").asText(),
					transaction.path("transactionCode").asText(),
					timezone.convertUsDateToKoreanDate(transaction.path("transactionDate").asText()),
					transaction.path("transactionPrice").asDouble()
				));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
