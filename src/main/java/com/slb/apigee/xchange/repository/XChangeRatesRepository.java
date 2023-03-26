package com.slb.apigee.xchange.repository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.slb.apigee.xchange.entity.BloombergXchangeRate;
import com.slb.apigee.xchange.entity.BloombergXchangeRateEntity;
import com.slb.apigee.xchange.entity.ExchangeRatesResponse;

@Repository
public class XChangeRatesRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(XChangeRatesRepository.class);

	private RestHighLevelClient restHighLevelClient;

	private ObjectMapper objectMapper;

	private static final ObjectMapper mapper = new ObjectMapper();

	private static final String INDEX_NAME = "bloomberg_exchangerates";
	private static final String TYPE = "_doc";

	public XChangeRatesRepository(ObjectMapper objectMapper, RestHighLevelClient restHighLevelClient) {
		this.objectMapper = objectMapper;
		this.restHighLevelClient = restHighLevelClient;
	}

	public boolean pingELK() throws IOException {
		return restHighLevelClient.ping(RequestOptions.DEFAULT);
	}

	public ExchangeRatesResponse getAllLatestRates(Integer perPage, Integer page) {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		queryBuilder
				.must(QueryBuilders.termQuery("date", new SimpleDateFormat("yyyy-MM-dd").format(getDateForELKCall())));
		sourceBuilder.query(queryBuilder);

		int offset = (page - 1) * perPage;
		sourceBuilder.from(offset);
		sourceBuilder.size(perPage);

		SearchRequest searchRequest = buildSearchRequest(INDEX_NAME, null);
		searchRequest.source(sourceBuilder);

		ExchangeRatesResponse resultPage = new ExchangeRatesResponse();

		try {
			SearchResponse response = restHighLevelClient.search(searchRequest);
			JsonNode result = mapper.readTree(response.toString());

			resultPage.setExchangeRates(mapToResultList(result));
			resultPage.setPageNum(page);
			Integer res = (int) ((response.getHits().getTotalHits() % perPage) == 0
					? response.getHits().getTotalHits() / perPage : response.getHits().getTotalHits() / perPage + 1);
			resultPage.setTotalPages(res);

		} catch (IOException e) {
			LOGGER.warn("Can not get ES search response. Exception: {}", e);
		}

		return resultPage;
	}

	public ExchangeRatesResponse getAllXchangeRatesByDate(Date date, Integer perPage, Integer page) {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		// sourceBuilder.query(QueryBuilders.matchAllQuery());
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		queryBuilder.must(QueryBuilders.termQuery("date", new SimpleDateFormat("yyyy-MM-dd").format(date)));
		sourceBuilder.query(queryBuilder);
		sourceBuilder.sort("ticker.keyword");
		int offset = (page - 1) * perPage;
		sourceBuilder.from(offset);
		sourceBuilder.size(perPage);

		SearchRequest searchRequest = buildSearchRequest(INDEX_NAME, null);
		searchRequest.source(sourceBuilder);

		ExchangeRatesResponse resultPage = new ExchangeRatesResponse();
		LOGGER.info("Query : {}", sourceBuilder);
		try {
			SearchResponse response = restHighLevelClient.search(searchRequest);
			JsonNode result = mapper.readTree(response.toString());

			resultPage.setExchangeRates(mapToResultList(result));
			resultPage.setPageNum(page);
			Integer res = (int) ((response.getHits().getTotalHits() % perPage) == 0
					? response.getHits().getTotalHits() / perPage : response.getHits().getTotalHits() / perPage + 1);
			resultPage.setTotalPages(res);

		} catch (IOException e) {
			LOGGER.warn("Can not get ES search response. Exception: {}", e);
		}

		return resultPage;
	}

	public ExchangeRatesResponse getAllXchangeRatesByDates(List<String> tickerSymbol, String uniqueIdentifierType,
			String uniqueIdentifierCode, String rateSource, Date fromDate, Date toDate, String priceType,
			Integer perPage, Integer page) {

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

		if (tickerSymbol != null && tickerSymbol.size() > 0) {
			for (String ticker : tickerSymbol) {
				queryBuilder.must(QueryBuilders.termQuery("ticker.keyword", ticker));
			}
		}
		if (uniqueIdentifierType != null && uniqueIdentifierType.length() > 1) {
			queryBuilder.must(QueryBuilders.termQuery("securityTyp.keyword", uniqueIdentifierType));
		}

		if (uniqueIdentifierCode != null && uniqueIdentifierCode.length() > 1) {
			queryBuilder.must(QueryBuilders.termQuery("idbbGlobal.keyword", uniqueIdentifierCode));
		}
		if (rateSource != null && rateSource.length() > 1) {
			queryBuilder.must(QueryBuilders.termQuery("fxPricingSource.keyword", rateSource));
		}
		if (fromDate != null && toDate != null) {
			queryBuilder.must(QueryBuilders.rangeQuery("date").from(new SimpleDateFormat("yyyy-MM-dd").format(fromDate))
					.to(new SimpleDateFormat("yyyy-MM-dd").format(toDate)));
		}
		sourceBuilder.query(queryBuilder);
		sourceBuilder.sort("date").sort("ticker.keyword");
		int offset = (page - 1) * perPage;
		sourceBuilder.from(offset);
		sourceBuilder.size(perPage);

		SearchRequest searchRequest = buildSearchRequest(INDEX_NAME, null);
		searchRequest.source(sourceBuilder);

		ExchangeRatesResponse resultPage = new ExchangeRatesResponse();
		LOGGER.info("Query : {}", sourceBuilder);
		try {
			SearchResponse response = restHighLevelClient.search(searchRequest);
			JsonNode result = mapper.readTree(response.toString());

			resultPage.setExchangeRates(mapToResultList(result));
			resultPage.setPageNum(page);
			Integer res = (int) ((response.getHits().getTotalHits() % perPage) == 0
					? response.getHits().getTotalHits() / perPage : response.getHits().getTotalHits() / perPage + 1);
			resultPage.setTotalPages(res);

		} catch (IOException e) {
			LOGGER.warn("Can not get ES search response. Exception: {}", e);
		}

		return resultPage;
	}

	public List<BloombergXchangeRate> search(List<String> tickerSymbol, String uniqueIdentifierType,
			String uniqueIdentifierCode, String rateSource, Date rateDate, String priceType) {
		BoolQueryBuilder tickerQueryBuilder = QueryBuilders.boolQuery();
		BoolQueryBuilder uniqueIdQueryBuilder = QueryBuilders.boolQuery();
		String callType = "";
		if (tickerSymbol != null && tickerSymbol.size() > 0) {
			callType = "ticker";
			for (String ticker : tickerSymbol) {
				tickerQueryBuilder.should(QueryBuilders.termQuery("ticker.keyword", ticker));
			}
		} else if (uniqueIdentifierType != null && uniqueIdentifierType.length() > 1 && uniqueIdentifierCode != null
				&& uniqueIdentifierCode.length() > 1) {
			callType = "uniqueId";
			uniqueIdQueryBuilder.must(QueryBuilders.termQuery("idbbGlobal.keyword", uniqueIdentifierCode));
			uniqueIdQueryBuilder.must(QueryBuilders.termQuery("securityTyp.keyword", uniqueIdentifierType));
		}

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		if (rateDate == null) {
			queryBuilder.must(
					QueryBuilders.termQuery("date", new SimpleDateFormat("yyyy-MM-dd").format(getDateForELKCall())));
		}
 
		if (rateSource != null && rateSource.length() > 1) {
			queryBuilder.must(QueryBuilders.termQuery("fxPricingSource.keyword", rateSource));
		}
		if (rateDate != null) {
			queryBuilder.must(QueryBuilders.termQuery("date", new SimpleDateFormat("yyyy-MM-dd").format(rateDate)));
		}
		if (callType == "ticker") {

			queryBuilder.must(tickerQueryBuilder);
 		} else if (callType == "uniqueId") {
			queryBuilder.must(uniqueIdQueryBuilder);
 		}
		sourceBuilder.query(queryBuilder).size(1000);
		SearchRequest searchRequest = buildSearchRequest(INDEX_NAME, null);
		searchRequest.source(sourceBuilder);

		List<BloombergXchangeRate> resultPage = new ArrayList<>();
		LOGGER.info("Query : {}", sourceBuilder);
		try {
			SearchResponse response = restHighLevelClient.search(searchRequest);
			JsonNode result = mapper.readTree(response.toString());
			resultPage = mapToResultList(result);

		} catch (IOException e) {
			LOGGER.warn("Can not get ES search response. Exception: {}", e);
		}

		return resultPage;
	}

	private Date getDateForELKCall() {
		LocalTime now = LocalTime.now();
		LocalTime limit = LocalTime.parse("23:00");
		if (now.isAfter(limit))
			return today();
		else
			return yesterday();
	}

	private Date today() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0);
		return cal.getTime();
	}

	private Date yesterday() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	private SearchRequest buildSearchRequest(String index, String type) {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices(index);

		if (type != null)
			searchRequest.types(type);

		return searchRequest;
	}

	private List<BloombergXchangeRate> mapToResultList(JsonNode result) {

		if (result == null) {
			return Collections.emptyList();
		}

		JsonNode hits = result.get("hits");

		int total = hits.get("total").asInt();
		if (total == 0) {
			return Collections.emptyList();
		}

		ArrayNode hitsArray = (ArrayNode) hits.get("hits");
		List<BloombergXchangeRate> resultList = new ArrayList<>();
		for (JsonNode hit : hitsArray) {
			resultList.add(hitToResult(hit));
		}

		return resultList;
	}

	private BloombergXchangeRate hitToResult(JsonNode hit) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		JsonNode jsonSource = hit.get("_source");
		BloombergXchangeRateEntity entity = mapper.convertValue(jsonSource, BloombergXchangeRateEntity.class);

		BloombergXchangeRate rate = new BloombergXchangeRate();

		rate.setTicker(entity.getTicker());
		rate.setName(entity.getRateName());
		rate.setIdBBGlobal(entity.getIdbbGlobal());
		rate.setSecurityType(entity.getSecurityTyp());
		rate.setPxMid(entity.getPxMid());
		rate.setPxLst(entity.getPxLast());
		rate.setPxAsk(entity.getPxAsk());
		rate.setPxBid(entity.getPxBid());
		rate.setPxHigh(entity.getPxHigh());
		rate.setPxLow(entity.getPxLow());
		rate.setLastUpdate(entity.getLastUpdate());
		rate.setLastUpdateDate(simpleDateFormat.format(entity.getDate()));
		rate.setQuoteFactor(entity.getQuoteFactor());
		rate.setFxPricingSource(entity.getFxPricingSource());
		rate.setBsTier1CapRatio(entity.getBsTier1CapRatio());

		return rate;
	}

}
