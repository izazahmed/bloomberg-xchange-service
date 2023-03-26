package com.slb.apigee.xchange.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
         <dlws:fields>
            <dlws:field>NAME</dlws:field>
            <dlws:field>TICKER</dlws:field>
            <dlws:field>ID_BB_GLOBAL</dlws:field>
            <dlws:field>SECURITY_TYP</dlws:field>
            <dlws:field>PX_MID</dlws:field>
            <dlws:field>PX_LAST</dlws:field>
            <dlws:field>PX_ASK</dlws:field>
            <dlws:field>PX_BID</dlws:field>
            <dlws:field>PX_HIGH</dlws:field>
            <dlws:field>PX_LOW</dlws:field>
            <dlws:field>LAST_UPDATE</dlws:field>
            <dlws:field>LAST_UPDATE_DT</dlws:field>
            <dlws:field>QUOTE_FACTOR</dlws:field>
            <dlws:field>PRICING_SOURCE</dlws:field>
            <dlws:field>FX_PRICING_SOURCE</dlws:field>
         </dlws:fields>

 */
@JsonPropertyOrder({ "ticker", "securityType", "idBBGlobal", "fxPricingSource", "lastUpdateDate", "lastUpdate", "pxAsk",
		"pxBid", "pxHigh", "pxLow", "pxLst" , "bsTier1CapRatio" })
public class BloombergXchangeRate {

	@JsonProperty("tickerSymbol")
	private String ticker;

	@JsonProperty("uniqueIdentifierType")
	private String securityType;

	@JsonProperty("uniqueIdentifierCode")
	private String idBBGlobal;

	@JsonProperty("rateSource")
	private String fxPricingSource;

	@JsonProperty("rateDate")
	private String lastUpdateDate;

	@JsonProperty("rateTime")
	private String lastUpdate;

	@JsonProperty("askRate")
	private String pxAsk;

	@JsonProperty("bidRate")
	private String pxBid;

	@JsonProperty("highRate")
	private String pxHigh;

	@JsonProperty("lowRate")
	private String pxLow;

	@JsonProperty("lastRate")
	private String pxLst;

	@JsonProperty("bsTier1CapRatio")
	private String bsTier1CapRatio;


	
	@JsonIgnore
	private String pxMid;

	@JsonIgnore
	private String quoteFactor;

	@JsonIgnore
	private String name;

	public void setPxAsk(String pxAsk) {
		this.pxAsk = pxAsk;
	}

	public String getPxAsk() {
		return pxAsk;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getIdBBGlobal() {
		return idBBGlobal;
	}

	public void setIdBBGlobal(String idBBGlobal) {
		this.idBBGlobal = idBBGlobal;
	}

	public String getSecurityType() {
		return securityType;
	}

	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}

	public String getPxMid() {
		return pxMid;
	}

	public void setPxMid(String pxMid) {
		this.pxMid = pxMid;
	}

	public String getPxLst() {
		return pxLst;
	}

	public void setPxLst(String pxLst) {
		this.pxLst = pxLst;
	}

	public String getPxBid() {
		return pxBid;
	}

	public void setPxBid(String pxBid) {
		this.pxBid = pxBid;
	}

	public String getPxHigh() {
		return pxHigh;
	}

	public void setPxHigh(String pxHigh) {
		this.pxHigh = pxHigh;
	}

	public String getPxLow() {
		return pxLow;
	}

	public void setPxLow(String pxLow) {
		this.pxLow = pxLow;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getQuoteFactor() {
		return quoteFactor;
	}

	public void setQuoteFactor(String quoteFactor) {
		this.quoteFactor = quoteFactor;
	}

	public String getFxPricingSource() {
		return fxPricingSource;
	}

	public void setFxPricingSource(String fxPricingSource) {
		this.fxPricingSource = fxPricingSource;
	}
	public String getBsTier1CapRatio() {
		return bsTier1CapRatio;
	}

	public void setBsTier1CapRatio(String bsTier1CapRatio) {
		this.bsTier1CapRatio = bsTier1CapRatio;
	}

	@Override
	public String toString() {
		return "BloombergXchangeRate [ticker=" + ticker + ", securityType=" + securityType + ", idBBGlobal="
				+ idBBGlobal + ",  fxPricingSource=" + fxPricingSource + ", lastUpdateDate=" + lastUpdateDate
				+ ", lastUpdate=" + lastUpdate + ", pxAsk=" + pxAsk + ", pxBid=" + pxBid + ", pxHigh=" + pxHigh
				+ ", pxLow=" + pxLow + ", pxLst=" + pxLst + ", bsTier1CapRatio="
						+ bsTier1CapRatio + ", pxMid=" + pxMid + ", quoteFactor=" + quoteFactor
				+ ", name=" + name + "]";
	}

}
