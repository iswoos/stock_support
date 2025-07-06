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
import com.stock.stock_support.adapter.in.controller.news.dto.request.GetMarketNews;
import com.stock.stock_support.application.port.in.usecase.news.GetMarketNewsUseCase;
import com.stock.stock_support.application.port.in.usecase.news.dto.response.MarketNewsInfo;
import com.stock.stock_support.global.common.Timezone;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetMarketNewsService implements GetMarketNewsUseCase {

	@Value("${finnhub.api-key}")
	private String api_key;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Timezone timezone;

	@Override
	public List<MarketNewsInfo> getMarketNews(GetMarketNews getMarketNews) {
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
					news.path("url").asText()
				));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
