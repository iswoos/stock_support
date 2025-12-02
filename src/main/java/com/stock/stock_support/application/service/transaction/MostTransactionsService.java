package com.stock.stock_support.application.service.transaction;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.stock.stock_support.application.port.in.usecase.transaction.GetMostTransactionsUseCase;
import com.stock.stock_support.application.port.in.usecase.transaction.dto.response.MostTransactionsInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MostTransactionsService implements GetMostTransactionsUseCase {

	@Value("${financialmodelingprep.api-key}")
	private String financialmodelingprep_api_key;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public List<MostTransactionsInfo> getMostTransactions() {
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI("https://financialmodelingprep.com/stable/most-actives?apikey=%s".formatted(financialmodelingprep_api_key)))
				.GET()
				.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				System.err.println("API 호출 실패: " + response.body());
			}
			ArrayNode arrayNode = (ArrayNode) objectMapper.readTree(response.body());
			List<MostTransactionsInfo> result = new ArrayList<>();
			for (JsonNode node : arrayNode) {
				result.add(new MostTransactionsInfo(
					node.path("symbol").asText(),
					node.path("name").asText(),
					node.path("price").asDouble(),
					node.path("change").asDouble(),
					node.path("changesPercentage").asDouble(),
					node.path("exchange").asText()
				));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
