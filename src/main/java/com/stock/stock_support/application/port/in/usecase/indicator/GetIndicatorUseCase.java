package com.stock.stock_support.application.port.in.usecase.indicator;

import java.util.List;

import com.stock.stock_support.adapter.in.controller.indicator.dto.request.GetIndicatorSeriesId;
import com.stock.stock_support.application.port.in.usecase.indicator.dto.response.ObservationsInfo;

public interface GetIndicatorUseCase {
	List<ObservationsInfo> getIndicator(GetIndicatorSeriesId getIndicator);
}
