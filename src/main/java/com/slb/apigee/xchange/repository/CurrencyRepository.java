package com.slb.apigee.xchange.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.ParsedTopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slb.apigee.xchange.entity.Currency;

@Repository
public class CurrencyRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyRepository.class);

	private RestHighLevelClient client;

	private ObjectMapper objectMapper;

	private static final String INDEX = "bloomberg_currencies";
	private static final String TYPE = "_doc";

	public CurrencyRepository(ObjectMapper objectMapper, RestHighLevelClient restHighLevelClient) {
		this.objectMapper = objectMapper;
		this.client = restHighLevelClient;
	}

	public String create(Currency newCurrency) throws Exception {
		IndexRequest indexRequest = new IndexRequest(INDEX, TYPE)
				.source(objectMapper.writeValueAsString(newCurrency), XContentType.JSON).id(newCurrency.getCurrency());
		IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
		return indexResponse.getResult().name();
	}

	public void createAll(List<Currency> currencyList) throws Exception {

		BulkRequest request = new BulkRequest();

		for (Currency currency : currencyList) {
			request.add(new IndexRequest(INDEX, TYPE)
					.source(objectMapper.writeValueAsString(currency), XContentType.JSON).id(currency.getCurrency()));
		}
		BulkResponse bulkresp = client.bulk(request);
		if (bulkresp.hasFailures()) {
			for (BulkItemResponse bulkItemResponse : bulkresp) {
				if (bulkItemResponse.isFailed()) {
					BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
					System.out.println("Error " + failure.toString());
				}
			}
		}
	}

	public String deleteCurrency2(String currency) throws Exception {
		DeleteByQueryRequest delete = new DeleteByQueryRequest(INDEX)
				.setQuery(QueryBuilders.matchQuery("currency", currency));
		BulkByScrollResponse response = client.deleteByQuery(delete, RequestOptions.DEFAULT);
		return response.getDeleted() >= 1 ? "Successfully deleted" : "not deleted";
	}

	public List<Currency> search(String currency) throws Exception {
		SearchRequest searchRequest = buildSearchRequest(INDEX, TYPE);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		if (currency != null) {
			if (currency.contains("*"))
				searchSourceBuilder.query(QueryBuilders.wildcardQuery("currency.keyword", currency)).size(10000);
			else
				searchSourceBuilder.query(QueryBuilders.matchQuery("currency", currency)).size(10000);;
		} else
			searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(10000);
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		return getSearchResult(searchResponse);
	}

	private SearchRequest buildSearchRequest(String index, String type) {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices(index);
		searchRequest.types(type);
		return searchRequest;
	}

	private List<Currency> getSearchResult(SearchResponse response) {
		SearchHit[] searchHit = response.getHits().getHits();
		List<Currency> list = new ArrayList<>();
		for (SearchHit hit : searchHit) {
			list.add(objectMapper.convertValue(hit.getSourceAsMap(), Currency.class));
		}
		return list;
	}
	
	
	
}
