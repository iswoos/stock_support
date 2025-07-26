package com.stock.stock_support.application.service.calendar;

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
import com.stock.stock_support.adapter.in.controller.calendar.dto.request.GetCalendar;
import com.stock.stock_support.application.port.in.usecase.calendar.GetIpoCalendarUseCase;
import com.stock.stock_support.application.port.in.usecase.calendar.dto.response.IpoCalendarInfo;
import com.stock.stock_support.application.port.in.usecase.indicator.dto.response.ObservationsInfo;
import com.stock.stock_support.application.port.in.usecase.news.dto.response.CompanyNewsInfo;
import com.stock.stock_support.global.common.Timezone;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetIpoCalendarService implements GetIpoCalendarUseCase {

	@Value("${finnhub.api-key}")
	private String api_key;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Timezone timezone;

	@Override
	public List<IpoCalendarInfo> getIpoCalendar(GetCalendar getCalendar) {
		try {
			String from = getCalendar.from();
			String to = getCalendar.to();

			if (from == null || from.isBlank()) {
				from = timezone.oneMonthAgoDateUs();
			}
			if (to == null || to.isBlank()) {
				to = timezone.nowDateUs();
			}

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(new URI("https://finnhub.io/api/v1/calendar/ipo?from=%s&to=%s&token=%s".formatted(from, to, api_key)))
				.GET()
				.build();

			HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			// Jackson으로 JSON 파싱
			JsonNode rootNode = objectMapper.readTree(response.body());
			JsonNode ipos = rootNode.path("ipoCalendar"); // ipoCalendar 배열 접근

			List<IpoCalendarInfo> result = new ArrayList<>();
			for (JsonNode ipo : ipos) {
				result.add(new IpoCalendarInfo(
					ipo.path("date").asText(),
					ipo.path("exchange").asText(),
					ipo.path("name").asText(),
					ipo.path("numberOfShares").asLong(),
					ipo.path("price").asText(),
					ipo.path("status").asText(),
					ipo.path("symbol").asText(),
					ipo.path("totalSharesValue").asLong()
				));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
