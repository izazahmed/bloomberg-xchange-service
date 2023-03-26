package com.slb.apigee.xchange.service;

import java.util.List;

import org.elasticsearch.search.aggregations.metrics.tophits.ParsedTopHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slb.apigee.xchange.entity.Currency;
import com.slb.apigee.xchange.repository.CurrencyRepository;

@Service
public class CurrencyService {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CurrencyRepository currencyRepository;

	public String add(Currency currency) throws Exception {
		return currencyRepository.create(currency);
	}
	public void addAll(List<Currency> currency) throws Exception {
		currencyRepository.createAll(currency);
	}
	
	public List<Currency> findByCurrency(String currency) throws Exception {
		return currencyRepository.search(currency);
	}

	public String delete(String currency) throws Exception {
		return currencyRepository.deleteCurrency2(currency);
	}
	
	public List<Currency> getAll() throws Exception {
		return currencyRepository.search(null);
	}
	

}
