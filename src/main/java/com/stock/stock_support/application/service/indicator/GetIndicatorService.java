package com.stock.stock_support.application.service.indicator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.stock_support.adapter.in.controller.indicator.dto.request.GetIndicator;
import com.stock.stock_support.application.port.in.usecase.indicator.GetIndicatorUseCase;
import com.stock.stock_support.application.port.in.usecase.indicator.dto.response.ObservationsInfo;
import com.stock.stock_support.global.common.Timezone;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetIndicatorService implements GetIndicatorUseCase {

	@Value("${fred.api-key}")
	private String api_key;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Timezone timezone;

	@Override
	public List<ObservationsInfo> getIndicator(GetIndicator getIndicator) {
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI("https://api.stlouisfed.org/fred/series/observations?series_id=%s&api_key=%s&file_type=json".formatted(getIndicator.series_id(), api_key)))
				.GET()
				.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			JsonNode observations = objectMapper.readTree(response.body()).get("observations");

			List<ObservationsInfo> result = new ArrayList<>();
			for (JsonNode observation : observations) {
				String date = observation.path("date").asText();
				String value = observation.path("value").asText();

				String localTime = timezone.toKoreaDate(date);

				result.add(new ObservationsInfo(localTime, value));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}