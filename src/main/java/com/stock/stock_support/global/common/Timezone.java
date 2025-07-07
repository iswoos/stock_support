package com.stock.stock_support.global.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class Timezone {

	private final ZoneId defaultZoneId;
	private final ZoneId usZoneId; // 미국 기준 시간대

	private final DateTimeFormatter dateTimeFormatter;
	private final DateTimeFormatter dateFormatter;

	public Timezone() {
		this.defaultZoneId = ZoneId.of("Asia/Seoul");
		this.usZoneId = ZoneId.of("America/New_York");
		this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	}

	public String formatEpochSecondsToLocalTime(long epochSeconds) {
		LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), defaultZoneId);
		return localDateTime.format(dateTimeFormatter);
	}

	public String convertUsDateToKoreanDate(String usDateStr) {
		LocalDate localDate = LocalDate.parse(usDateStr, dateFormatter);
		var usZoned = localDate.atStartOfDay(usZoneId);
		var koreanZoned = usZoned.withZoneSameInstant(defaultZoneId);
		return koreanZoned.format(dateFormatter);
	}

	public LocalDateTime toLocalDateTime(long epochSeconds) {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), defaultZoneId);
	}

	public String nowDateUs() {
		return LocalDate.now(usZoneId).format(dateFormatter);
	}

	public String oneMonthAgoDateUs() {
		return LocalDate.now(usZoneId).minusMonths(1).format(dateFormatter);
	}

	public String toKoreaDate(String fredDateStr) {
		LocalDate localDate = LocalDate.parse(fredDateStr, dateFormatter);
		var utcZoneId = ZoneId.of("UTC");
		var utcZoned = localDate.atStartOfDay(utcZoneId);
		var koreanZoned = utcZoned.withZoneSameInstant(defaultZoneId);
		return koreanZoned.format(dateFormatter);
	}
}
