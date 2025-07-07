package com.stock.stock_support.application.port.in.usecase.indicator;

import java.util.List;

import com.stock.stock_support.adapter.in.controller.indicator.dto.request.GetIndicator;
import com.stock.stock_support.application.port.in.usecase.indicator.dto.response.ObservationsInfo;

public interface GetIndicatorUseCase {
	List<ObservationsInfo> getIndicator(GetIndicator getIndicator);
}
