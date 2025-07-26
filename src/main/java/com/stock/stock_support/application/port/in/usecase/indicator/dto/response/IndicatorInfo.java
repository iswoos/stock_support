package com.stock.stock_support.application.port.in.usecase.indicator.dto.response;

import java.util.List;

public record IndicatorInfo(
	String realtime_start,
	String realtime_end,
	String observation_start,
	String observation_end,
	String units,
	int output_type,
	String file_type,
	String order_by,
	String sort_order,
	int count,
	int offset,
	int limit,
	List<Observation> observations
) {
	public record Observation(
		String realtime_start,
		String realtime_end,
		String date,
		String value
	) {}
}
