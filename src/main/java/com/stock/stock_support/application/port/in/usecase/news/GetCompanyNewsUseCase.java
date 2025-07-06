package com.stock.stock_support.application.port.in.usecase.news;

import java.util.List;

import com.stock.stock_support.adapter.in.controller.news.dto.request.GetCompanyNews;
import com.stock.stock_support.application.port.in.usecase.news.dto.response.CompanyNewsInfo;

public interface GetCompanyNewsUseCase {
	List<CompanyNewsInfo> getCompanyNews(GetCompanyNews getCompanyNews);
}
