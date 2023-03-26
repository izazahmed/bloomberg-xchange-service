package com.slb.apigee.xchange.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.slb.apigee.xchange.service.XChangeRatesService;

@Component
public class ScheduledPingELK {
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private XChangeRatesService xchangeRatesService;

	/**
	 * 
	 * Scheduler task to PING Elasticsearch for every 5 mints
	 *
	 */
	//@Scheduled(cron = "0 0/5 * * * ?")
	public void pingElasticSearch() {
		LOG.info("##### Ping ELK ");
		try {

			if (!xchangeRatesService.pingELK()) {
				LOG.info("************** Ping FAILED *************** ");
			}

		} catch (Exception e) {
			LOG.error("##### Ping ELK failed with exception ", e);
			e.printStackTrace();
		}

	}

}
