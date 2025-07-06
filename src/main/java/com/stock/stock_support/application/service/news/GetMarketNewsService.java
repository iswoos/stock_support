package com.stock.stock_support.application.service.news;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.stock_support.adapter.in.controller.news.dto.request.GetMarketNews;
import com.stock.stock_support.application.port.in.usecase.news.GetMarketNewsUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetMarketNewsService implements GetMarketNewsUseCase {

	@Value("${finnhub.api-key}")
	private String api_key;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String getMarketNews(GetMarketNews getMarketNews) {
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(new URI("https://finnhub.io/api/v1/news?category=%s&token=%s".formatted(getMarketNews.category(), api_key)))
				.GET()
				.build();

			HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			// Jackson으로 JSON 파싱
			JsonNode articles = objectMapper.readTree(response.body());

			StringBuilder sb = new StringBuilder();
			int count = 0;

			for (JsonNode news : articles) {
				if ("MarketWatch".equalsIgnoreCase(news.path("source").asText())) {
					sb.append("📰 ").append(news.path("headline").asText()).append("\n")
						.append("🔗 ").append(news.path("url").asText()).append("\n\n");
					count++;
				}
				if (count >= 10) break;
			}

			if (count == 0) {
				return "⚠️ MarketWatch 기사 없음.";
			}

			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "뉴스 불러오기 실패: " + e.getMessage();
		}
	}
}
