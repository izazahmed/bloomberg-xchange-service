package com.slb.apigee.xchange.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slb.apigee.xchange.entity.BloombergXchangeRate;
import com.slb.apigee.xchange.entity.ExchangeRatesResponse;
import com.slb.apigee.xchange.repository.XChangeRatesRepository;

@Service
public class XChangeRatesService {

	@Autowired
	private XChangeRatesRepository xchangeRatesRepository;

	public boolean pingELK() throws IOException {
		return xchangeRatesRepository.pingELK();
	}

	public ExchangeRatesResponse getAllXchangeRatesByDate(Date date, Integer perPage, Integer page) {
		return xchangeRatesRepository.getAllXchangeRatesByDate(date, perPage, page);
	}

	public ExchangeRatesResponse getAllXchangeRatesByDates(List<String> tickerSymbol, String uniqueIdentifierType,
			String uniqueIdentifierCode, String rateSource, Date fromDate, Date toDate, String priceType,
			Integer perPage, Integer page) {
		return xchangeRatesRepository.getAllXchangeRatesByDates(tickerSymbol, uniqueIdentifierType,
				uniqueIdentifierCode, rateSource, fromDate, toDate, priceType, perPage, page);
	}

	public ExchangeRatesResponse getAllLatestRates(Integer perPage, Integer page) {
		return xchangeRatesRepository.getAllLatestRates(perPage, page);
	}

	public List<BloombergXchangeRate> search(List<String> tickerSymbol, String uniqueIdentifierType,
			String uniqueIdentifierCode, String rateSource, Date rateDate, String priceType) {
		return xchangeRatesRepository.search(tickerSymbol, uniqueIdentifierType, uniqueIdentifierCode, rateSource,
				rateDate, priceType);
	}

}
