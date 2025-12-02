package com.stock.stock_support.application.service.transaction;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.stock_support.adapter.in.controller.transaction.dto.GetStockInstitutionTransactions;
import com.stock.stock_support.application.port.in.usecase.transaction.GetInstitutionTransactionsUseCase;
import com.stock.stock_support.application.port.in.usecase.transaction.dto.response.StockInstitutionTransactionsInfo;
import com.stock.stock_support.global.common.Timezone;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetInstitutionTransactionsService implements GetInstitutionTransactionsUseCase {

	private static final String WHALE_WISDOM_API_URL = "https://whalewisdom.com/shell/command.json";
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private static final int MAX_QUARTERS_TO_SEARCH = 8; // 무료 사용자는 최대 8분기 데이터 접근 가능
	private static final int FREE_USER_CURRENT_QUARTER_EXCLUSION = 1; // 현재 분기 제외

	@Value("${whalewisdom.api-shared-key}")
	private String apiSharedKey;

	@Value("${whalewisdom.api-secret-key}")
	private String apiSecretKey;

	@Value("${whalewisdom.is-free-user:true}")
	private boolean isFreeUser;

	private final ObjectMapper objectMapper;
	private final Timezone timezone;
	private final HttpClient httpClient = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(10))
		.build();

	@Override
	public List<StockInstitutionTransactionsInfo> getInstitutionTransactions(GetStockInstitutionTransactions request) {
		try {
			Integer stockId = getStockIdBySymbol(request.symbol());
			if (stockId == null) {
				log.warn("Stock ID not found for symbol: {}", request.symbol());
				return Collections.emptyList();
			}


			List<Integer> availableQuarters = getAvailableQuarters();
			if (availableQuarters.isEmpty()) {
				log.warn("No available quarters found");
				return Collections.emptyList();
			}

			log.info("Available quarters for symbol {}: {}", request.symbol(), availableQuarters);


			for (Integer quarterId : availableQuarters) {
				List<StockInstitutionTransactionsInfo> result = fetchHoldersData(stockId, request.symbol(), quarterId);
				if (!result.isEmpty()) {
					log.info("Successfully fetched data from quarter {} for symbol: {}", quarterId, request.symbol());
					return result;
				}
			}

			log.warn("No data found in any available quarters for symbol: {}", request.symbol());
			return Collections.emptyList();

		} catch (Exception e) {
			log.error("Failed to fetch institutional transactions for symbol: {}", request.symbol(), e);
			return Collections.emptyList();
		}
	}

	/**
	 * WhaleWisdom API의 quarters 명령을 사용하여 사용 가능한 분기 목록을 가져옴
	 */
	private List<Integer> getAvailableQuarters() {
		try {
			String args = "{\"command\":\"quarters\"}";
			String timestamp = ZonedDateTime.now(ZoneOffset.UTC)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
			String signature = generateSignature(args, timestamp);

			String encodedArgs = URLEncoder.encode(args, StandardCharsets.UTF_8);
			String encodedTimestamp = URLEncoder.encode(timestamp, StandardCharsets.UTF_8);
			String url = String.format("%s?args=%s&api_shared_key=%s&api_sig=%s&timestamp=%s",
				WHALE_WISDOM_API_URL, encodedArgs, apiSharedKey, signature, encodedTimestamp);

			URI uri = new URI(url);
			HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(uri)
				.GET()
				.build();

			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != 200) {
				log.error("Quarters API returned status code: {}", response.statusCode());
				return Collections.emptyList();
			}

			return parseQuartersResponse(response.body());

		} catch (Exception e) {
			log.error("Failed to fetch available quarters", e);
			return Collections.emptyList();
		}
	}

	/**
	 * quarters API 응답을 파싱하여 사용 가능한 분기 ID 목록 반환
	 */
	private List<Integer> parseQuartersResponse(String responseBody) {
		try {
			JsonNode root = objectMapper.readTree(responseBody);
			List<Integer> availableQuarters = new ArrayList<>();

			// 응답 구조에 따라 적절히 파싱
			JsonNode quartersNode = root;
			if (root.isArray()) {
				quartersNode = root;
			} else if (root.has("quarters")) {
				quartersNode = root.get("quarters");
			} else if (root.has("data")) {
				quartersNode = root.get("data");
			}

			if (quartersNode.isArray()) {
				for (JsonNode quarter : quartersNode) {
					Integer quarterId = quarter.path("id").asInt();
					boolean hasAccess = quarter.path("has_access").asBoolean(true); // 기본값 true

					if (quarterId > 0 && hasAccess) {
						availableQuarters.add(quarterId);
					}
				}
			}


			availableQuarters.sort((a, b) -> b.compareTo(a));

			// 무료 사용자 제한 적용
			if (isFreeUser) {
				// 현재 분기 제외하고 최대 8분기만
				int maxSize = Math.min(MAX_QUARTERS_TO_SEARCH, availableQuarters.size());
				availableQuarters = availableQuarters.subList(FREE_USER_CURRENT_QUARTER_EXCLUSION,
					Math.min(FREE_USER_CURRENT_QUARTER_EXCLUSION + maxSize, availableQuarters.size()));
			}

			log.info("Parsed {} available quarters (free user: {})", availableQuarters.size(), isFreeUser);
			return availableQuarters;

		} catch (Exception e) {
			log.error("Failed to parse quarters response: {}", responseBody, e);
			return Collections.emptyList();
		}
	}

	private List<StockInstitutionTransactionsInfo> fetchHoldersData(Integer stockId, String symbol, Integer quarterId) {
		try {
			String args = String.format("{\"command\":\"holders\",\"stock_ids\":[%d],\"include_13d\":1,\"quarter_ids\":[%d]}",
				stockId, quarterId);

			String timestamp = ZonedDateTime.now(ZoneOffset.UTC)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
			String signature = generateSignature(args, timestamp);

			String encodedArgs = URLEncoder.encode(args, StandardCharsets.UTF_8);
			String encodedTimestamp = URLEncoder.encode(timestamp, StandardCharsets.UTF_8);
			String url = String.format("%s?args=%s&api_shared_key=%s&api_sig=%s&timestamp=%s",
				WHALE_WISDOM_API_URL, encodedArgs, apiSharedKey, signature, encodedTimestamp);

			log.info("Holders API URL for quarter {}: {}", quarterId, url);

			URI uri = new URI(url);
			HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(uri)
				.GET()
				.build();

			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			log.info("Holders API Response Status for quarter {} symbol {}: {}", quarterId, symbol, response.statusCode());
			log.debug("Holders API Response Body: {}", response.body());

			if (response.statusCode() != 200) {
				log.error("WhaleWisdom API returned status code: {} for quarter {} symbol: {}",
					response.statusCode(), quarterId, symbol);
				return Collections.emptyList();
			}

			// 응답에 에러가 있는지 확인
			JsonNode errorCheck = objectMapper.readTree(response.body());
			if (errorCheck.has("errors")) {
				JsonNode errors = errorCheck.get("errors");
				log.warn("API returned errors for quarter {} symbol {}: {}", quarterId, symbol, errors);
				return Collections.emptyList();
			}

			return parseHoldersResponse(response.body());

		} catch (Exception e) {
			log.error("Failed to fetch holders data for quarter {} symbol: {}", quarterId, symbol, e);
			return Collections.emptyList();
		}
	}

	private Integer getStockIdBySymbol(String symbol) {
		try {
			String args = String.format("{\"command\":\"stock_lookup\",\"symbol\":\"%s\"}", symbol);
			String timestamp = ZonedDateTime.now(ZoneOffset.UTC)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
			String signature = generateSignature(args, timestamp);

			String encodedArgs = URLEncoder.encode(args, StandardCharsets.UTF_8);
			String encodedTimestamp = URLEncoder.encode(timestamp, StandardCharsets.UTF_8);
			String url = String.format("%s?args=%s&api_shared_key=%s&api_sig=%s&timestamp=%s",
				WHALE_WISDOM_API_URL, encodedArgs, apiSharedKey, signature, encodedTimestamp);

			log.info("Stock Lookup API URL: {}", url);

			URI uri = new URI(url);
			HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(uri)
				.GET()
				.build();

			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			log.info("Stock Lookup Response Status for symbol {}: {}", symbol, response.statusCode());
			log.debug("Stock Lookup Response Body: {}", response.body());

			if (response.statusCode() != 200) {
				log.error("Stock lookup failed with status: {} for symbol: {}", response.statusCode(), symbol);
				return null;
			}

			JsonNode root = objectMapper.readTree(response.body());

			// 여러 가능한 응답 구조 처리
			JsonNode stocks = null;
			if (root.isArray()) {
				stocks = root;
			} else if (root.has("stocks")) {
				stocks = root.get("stocks");
			} else if (root.has("data")) {
				stocks = root.get("data");
			}

			if (stocks != null && stocks.isArray() && stocks.size() > 0) {
				JsonNode firstResult = stocks.get(0);
				int stockId = firstResult.path("id").asInt();
				log.info("Found stock ID: {} for symbol: {}", stockId, symbol);
				return stockId;
			}

			log.warn("No stock ID found in response for symbol: {}", symbol);
			return null;
		} catch (Exception e) {
			log.error("Failed to lookup stock ID for symbol: {}", symbol, e);
			return null;
		}
	}

	private String generateSignature(String args, String timestamp) throws NoSuchAlgorithmException, InvalidKeyException {
		String message = args + "\n" + timestamp;
		log.debug("Signature input message: {}", message);
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		SecretKeySpec secretKeySpec = new SecretKeySpec(apiSecretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA1_ALGORITHM);
		mac.init(secretKeySpec);
		byte[] hmacBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
		String signature = Base64.getEncoder().encodeToString(hmacBytes);
		log.debug("Generated signature: {}", signature);
		return signature;
	}

	private List<StockInstitutionTransactionsInfo> parseHoldersResponse(String responseBody) {
		try {
			JsonNode root = objectMapper.readTree(responseBody);
			log.debug("Parsing holders response: {}", responseBody);

			// WhaleWisdom API는 배열 형태로 직접 데이터를 반환할 수 있음
			JsonNode dataNode = root;
			if (root.isObject() && root.has("data")) {
				dataNode = root.path("data");
			}

			if (!dataNode.isArray()) {
				log.warn("Holders response is not in expected array format: {}", dataNode);
				return Collections.emptyList();
			}

			return StreamSupport.stream(dataNode.spliterator(), false)
				.map(this::mapToStockInstitutionTransactionsInfo)
				.filter(Objects::nonNull)
				.toList();

		} catch (Exception e) {
			log.error("Failed to parse holders response: {}", responseBody, e);
			return Collections.emptyList();
		}
	}

	private StockInstitutionTransactionsInfo mapToStockInstitutionTransactionsInfo(JsonNode holding) {
		try {
			// API 문서에 따른 필드 매핑
			String filerName = getStringValue(holding, "filer_name", "name");
			long shares = getLongValue(holding, "shares", "current_shares");
			long change = getLongValue(holding, "change", "shares_change");
			String filingDate = getStringValue(holding, "filing_date", "date");
			double marketValue = getDoubleValue(holding, "market_value", "value");
			double percentOfPortfolio = getDoubleValue(holding, "percent_of_portfolio", "portfolio_percent");
			String assetType = getStringValue(holding, "asset_type", "type");

			// 필수 필드 검증
			if (filerName.isEmpty()) {
				log.debug("Skipping holding with empty filer name: {}", holding);
				return null;
			}

			return StockInstitutionTransactionsInfo.builder()
				.filerName(filerName)
				.shares(shares)
				.change(change)
				.filingDate(timezone.convertUsDateToKoreanDate(filingDate))
				.marketValue(marketValue)
				.percentOfPortfolio(percentOfPortfolio)
				.assetType(assetType)
				.build();

		} catch (Exception e) {
			log.error("Failed to map holding data: {}", holding, e);
			return null;
		}
	}

	private String getStringValue(JsonNode node, String... fieldNames) {
		for (String fieldName : fieldNames) {
			if (node.has(fieldName) && !node.get(fieldName).isNull()) {
				String value = node.get(fieldName).asText();
				if (!value.isEmpty()) {
					return value;
				}
			}
		}
		return "";
	}

	private long getLongValue(JsonNode node, String... fieldNames) {
		for (String fieldName : fieldNames) {
			if (node.has(fieldName) && !node.get(fieldName).isNull()) {
				return node.get(fieldName).asLong();
			}
		}
		return 0L;
	}

	private double getDoubleValue(JsonNode node, String... fieldNames) {
		for (String fieldName : fieldNames) {
			if (node.has(fieldName) && !node.get(fieldName).isNull()) {
				return node.get(fieldName).asDouble();
			}
		}
		return 0.0;
	}
}