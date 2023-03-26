package com.slb.apigee.xchange.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangeRatesResponse {

  private List<BloombergXchangeRate> exchangeRates;
  private Integer pageNum;
  private Integer totalPages;

}
