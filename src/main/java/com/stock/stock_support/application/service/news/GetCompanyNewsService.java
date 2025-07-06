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
import com.stock.stock_support.adapter.in.controller.news.dto.request.GetCompanyNews;
import com.stock.stock_support.application.port.in.usecase.news.GetCompanyNewsUseCase;
import com.stock.stock_support.application.port.in.usecase.news.dto.response.CompanyNewsInfo;
import com.stock.stock_support.global.common.Timezone;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetCompanyNewsService implements GetCompanyNewsUseCase {

	@Value("${finnhub.api-key}")
	private String api_key;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Timezone timezone;

	@Override
	public List<CompanyNewsInfo> getCompanyNews(GetCompanyNews getCompanyNews) {
		try {
			String from = getCompanyNews.from();
			String to = getCompanyNews.to();

			if (from == null || from.isBlank()) {
				from = timezone.oneMonthAgoDateUs();
			}
			if (to == null || to.isBlank()) {
				to = timezone.nowDateUs();
			}

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(new URI("https://finnhub.io/api/v1/company-news?symbol=%s&from=%s&to=%s&token=%s".formatted(getCompanyNews.symbol(), from, to, api_key)))
				.GET()
				.build();

			HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			// Jackson으로 JSON 파싱
			JsonNode articles = objectMapper.readTree(response.body());
			List<CompanyNewsInfo> result = new ArrayList<>();
			for (JsonNode news : articles) {
				result.add(new CompanyNewsInfo(
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