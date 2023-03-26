package com.slb.apigee.xchange.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.slb.apigee.xchange.entity.Currency;
import com.slb.apigee.xchange.service.CurrencyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@Validated
@RequestMapping("/currencies")
public class CurrencyController {

	private static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);

	@Autowired
	private CurrencyService currencyService;

	@GetMapping
	public Object[] getAllCurrency() throws Exception {
		logger.info("##### get all currency");
		List<String> list = new ArrayList<String>();
		List<Currency> currencyList = new ArrayList<>();
		currencyList = currencyService.getAll();
		for (Currency c : currencyList)
			list.add(c.getCurrency());

		logger.info("#####  currency found :  {}", currencyList.size());
		return list.toArray();
	}

	@GetMapping("/{currency}")
	public List<Currency> getCurrency(@PathVariable String currency, HttpServletResponse response) throws Exception {
		if (!currency.contains("*")) {
			logger.info("#####  get by currency {}", currency);
			List<Currency> cc = currencyService.findByCurrency(currency);

			if (cc == null) {
				logger.warn("##### Didn't found given currency");
				response.setHeader("x-errorcode", "" + currency);
				response.setHeader("x-errormsg", "Didn't found given currency ");
				return null;
			} else {
				return cc;
			}
		} else {
			logger.info("#####  get all currencys by currency wild card {}", currency);
			List<String> list = new ArrayList<String>();
			return currencyService.findByCurrency(currency);

		}
	}

	/*
	 * @PostMapping public String addCurrency(@RequestBody Currency currency)
	 * throws Exception { logger.info("#####  Add currency  {}",
	 * currency.toString()); return currency.getCurrency() + " " +
	 * currencyService.add(currency) + "."; }
	 */

	@ApiOperation(value = "Add single or mutliple currencies along with cron expression to schedule."
			+ "The default cron expression to run at every day 23 hours UTC time is  --> [\"0 0 23 * * ?\"]")
	@PostMapping
	public String addCurrencies(@RequestBody List<Currency> currencies) throws Exception {
		logger.info("#####  Add currency  {}", currencies.toString());

		for (Currency currency : currencies) {
			// Validate CRON expression
			for (String cronExpression : currency.getCronexpressions()) {
				if (!CronSequenceGenerator.isValidExpression(cronExpression)) {
					throw new Exception("Invalid cron expression for " + currency.toString());
				}
			}
		}

		currencyService.addAll(currencies);
		return "Following currencies are added successfully " + currencies.toString();
	}

	@DeleteMapping("/{currency}")
	public String deleteCurrency(@PathVariable String currency) throws Exception {
		logger.info("#####  Delete currency {}", currency);

		return currency + " " + currencyService.delete(currency) + ".";
	}

	@RestController
	public class Controller {
		@RequestMapping(value = "execute", method = RequestMethod.GET)
		public String execute(HttpServletResponse response) {
			response.setHeader("Cache-Control", "no-cache");
			return "SUCCESS";
		}
	}

}
