package com.slb.apigee.xchange.controller;

import java.sql.SQLException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.slb.apigee.xchange.entity.BloombergXchangeRate;
import com.slb.apigee.xchange.entity.ExchangeRatesResponse;
import com.slb.apigee.xchange.exceptions.InvalidDateDataException;
import com.slb.apigee.xchange.exceptions.NotFoundException;
import com.slb.apigee.xchange.service.XChangeRatesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

@Api
@RestController
@RequestMapping
@Validated
public class XchangeRatesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(XchangeRatesController.class);
	private static final String MM_DD_YYYY = "MM/dd/yyyy";

	private static final int FIRST_PAGE = 1;
	private static final int PAGE_SIZE = 100;

	@Autowired
	private XChangeRatesService xchangeRatesService;

	Format formatter = new SimpleDateFormat(MM_DD_YYYY);

	 
	@GetMapping(path = "/exchangerates",produces = { "application/json", "application/xml" })
	public ExchangeRatesResponse getExchangeRatesByDates(
			@RequestParam(value = "ticker-symbol", required = false) List<String> tickerSymbol,
			@RequestParam(value = "unique-identifier-type", required = false) String uniqueIdentifierType,
			@RequestParam(value = "unique-identifier-code", required = false) String uniqueIdentifierCode,
			@RequestParam(value = "rate-source", required = false) String rateSource,
			@RequestParam(value = "from-date", required = true) @ApiParam(value = MM_DD_YYYY) @DateTimeFormat(pattern = MM_DD_YYYY) Date fromDate,
			@RequestParam(value = "to-date", required = true) @ApiParam(value = MM_DD_YYYY) @DateTimeFormat(pattern = MM_DD_YYYY) Date toDate,
			@RequestParam(value = "price-type", required = false) String priceType,
			@RequestParam(value = "page-number", required = false) final Integer pageNum)
			throws SQLException, NotFoundException, InvalidDateDataException {
		LOGGER.info(
				"#### Getting Xchange reates by Dates for tickerSymbol={}  uniqueIdentifierType={}  uniqueIdentifierCode={}  "
						+ "rateSource={}  rateSourceCode={} fromDate={} toDate={}  priceType={}",
				tickerSymbol, uniqueIdentifierType, uniqueIdentifierCode, rateSource, fromDate, toDate, priceType);

		
			if(compareDates(fromDate,toDate)){
				throw new InvalidDateDataException("Dates in request are not in proper format, to-date should be always after from-date.");
			}
		
		
		ExchangeRatesResponse response = xchangeRatesService.getAllXchangeRatesByDates(tickerSymbol,
				uniqueIdentifierType, uniqueIdentifierCode, rateSource, fromDate, toDate, priceType, PAGE_SIZE,
				pageNum != null ? pageNum : FIRST_PAGE);

		if (response.getExchangeRates().size() == 0)
			throw new NotFoundException("Didn't  found any results for given search query");

		LOGGER.info("#### Response List size {}", response.getExchangeRates().size());
		return response;
	}

	@GetMapping(path = "/exchangerate",produces = { "application/json", "application/xml" })
	public List<BloombergXchangeRate> exchangerate(
			@RequestParam(value = "ticker-symbol", required = false) List<String> tickerSymbol,
			@RequestParam(value = "unique-identifier-type", required = false) String uniqueIdentifierType,
			@RequestParam(value = "unique-identifier-code", required = false) String uniqueIdentifierCode,
			@RequestParam(value = "rate-source", required = false) String rateSource,
			@RequestParam(value = "rate-date", required = false) @ApiParam(value = MM_DD_YYYY) @DateTimeFormat(pattern = MM_DD_YYYY) Date rateDate,
			@RequestParam(value = "price-type", required = false) String priceType)
			throws SQLException, NotFoundException {
		LOGGER.info(
				"#### Getting Xchange reates for tickerSymbol={}  uniqueIdentifierType={}  uniqueIdentifierCode={}  "
						+ "rateSource={}  rateDate={}  priceType={}",
				tickerSymbol, uniqueIdentifierType, uniqueIdentifierCode, rateSource, rateDate, priceType);

		if ((tickerSymbol == null || tickerSymbol.size() == 0)
				&& (isEmptyStringValue(uniqueIdentifierType) || isEmptyStringValue(uniqueIdentifierCode))) {
			String message = "Please input a valid Ticker Symbol or a"
					+ " combination of uniqueIdentifierType and uniqueIdentifierCode";
			LOGGER.info(message);
			throw new IllegalArgumentException(message);

		}

		List<BloombergXchangeRate> responseList = xchangeRatesService.search(tickerSymbol, uniqueIdentifierType,
				uniqueIdentifierCode, rateSource, rateDate, priceType);

		if (responseList.size() == 0)
			throw new NotFoundException("Didn't  found any results for given search query");

		LOGGER.info("#### responseList {}", responseList.toString());
		return responseList;
	}

	@GetMapping(path = "/healthcheck")
	public final ResponseEntity<String> healthcheck() {
		return new ResponseEntity<>("UP", HttpStatus.OK);
	}

	public boolean compareDates(Date from, Date to) {
	  
		boolean flag=false;
	      if(from.compareTo(to) > 0) {
	    	  flag=true;
	      }
	      return flag;
	}
	
	private boolean isEmptyStringValue(String value) {
		return value == null || value.trim().length() == 0;
	}

}
