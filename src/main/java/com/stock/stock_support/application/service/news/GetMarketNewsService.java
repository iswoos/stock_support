package com.stock.stock_support.application.service.news;

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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.stock.stock_support.adapter.in.controller.news.dto.request.GetMarketNewsCategory;
import com.stock.stock_support.adapter.in.controller.news.dto.request.GetMarketNewsPageLimit;
import com.stock.stock_support.application.port.in.usecase.news.GetMarketNewsUseCase;
import com.stock.stock_support.application.port.in.usecase.news.dto.response.MarketNewsInfo;
import com.stock.stock_support.global.common.Timezone;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetMarketNewsService implements GetMarketNewsUseCase {

	@Value("${finnhub.api-key}")
	private String api_key;

	@Value("${financialmodelingprep.api-key}")
	private String financialmodelingprep_api_key;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Timezone timezone;

	@Override
	public List<MarketNewsInfo> GetMarketNewsByFinnhub(GetMarketNewsCategory getMarketNews) {
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(new URI("https://finnhub.io/api/v1/news?category=%s&token=%s".formatted(getMarketNews.category(), api_key)))
				.GET()
				.build();

			HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			// Jackson으로 JSON 파싱
			JsonNode articles = objectMapper.readTree(response.body());
			List<MarketNewsInfo> result = new ArrayList<>();
			for (JsonNode news : articles) {
				result.add(new MarketNewsInfo(
					news.path("category").asText(),
					timezone.formatEpochSecondsToLocalTime(news.path("datetime").asLong()),
					news.path("headline").asText(),
					String.valueOf(news.path("id").asLong()),
					news.path("image").asText(),
					news.path("related").asText(),
					news.path("source").asText(),
					news.path("summary").asText(),
					news.path("url").asText(),
					"Finnhub"
				));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<MarketNewsInfo> GetMarketNewsFmp(GetMarketNewsPageLimit getMarketNewsPageLimit) {
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI("https://financialmodelingprep.com/stable/fmp-articles?page=%s&limit=%s&apikey=%s".formatted(getMarketNewsPageLimit.page(), getMarketNewsPageLimit.limit(), financialmodelingprep_api_key)))
				.GET()
				.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				System.err.println("API 호출 실패: " + response.body());
			}
			ArrayNode arrayNode = (ArrayNode) objectMapper.readTree(response.body());
			List<MarketNewsInfo> result = new ArrayList<>();
			for (JsonNode node : arrayNode) {
				result.add(new MarketNewsInfo(
					"",
					timezone.formatEpochSecondsToLocalTime(node.path("date").asLong()),
					node.path("title").asText(),
					"",
					node.path("image").asText(),
					node.path("tickers").asText(),
					node.path("author").asText(),
					node.path("content").asText(),
					node.path("link").asText(),
					"FMP"
				));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
