package com.slb.apigee.xchange.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class BloombergXchangeRateEntity {

	private String ticker;
	private Date lastUpdateDt;
	private String fxPricingSource;
	private String rateName;
	private String idbbGlobal;
	private String securityTyp;
	private String pxMid;
	private String pxLast;
	private String pxAsk;
	private String pxBid;
	private String pxHigh;
	private String pxLow;	
	private String lastUpdate;
	private String quoteFactor;
	private String pricingSource;
	private String bsTier1CapRatio;
	private Date date;
}
