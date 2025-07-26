package com.stock.stock_support.application.port.in.usecase.calendar;

import java.util.List;

import com.stock.stock_support.adapter.in.controller.calendar.dto.request.GetCalendar;
import com.stock.stock_support.application.port.in.usecase.calendar.dto.response.IpoCalendarInfo;

public interface GetIpoCalendarUseCase {
	List<IpoCalendarInfo> getIpoCalendar(GetCalendar getCalendar);
}
