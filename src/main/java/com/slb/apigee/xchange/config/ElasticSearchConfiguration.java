package com.slb.apigee.xchange.config;

import static java.lang.System.getenv;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.ElasticsearchNodesSniffer;
import org.elasticsearch.client.sniff.NodesSniffer;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfiguration extends AbstractFactoryBean<RestHighLevelClient> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchConfiguration.class);

	private RestHighLevelClient restHighLevelClient;
	private Sniffer sniffer;

	@Autowired
	private DataSourceProperties datasourceProperties;

	final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
	

	@Override
	public void destroy() {
		try {
			if (sniffer != null) {
				logger.info("*************  CLOSING   sniffer ************* ");
				sniffer.close();
			}
			if (restHighLevelClient != null) {
				logger.info("*************  CLOSING   restHighLevelClient ************* ");
				restHighLevelClient.close();
			}
		} catch (final Exception e) {
			logger.error("Error closing ElasticSearch client: ", e);
		}
	}

	@Override
	public Class<RestHighLevelClient> getObjectType() {
		return RestHighLevelClient.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@Override
	public RestHighLevelClient createInstance() {
		return buildClient();
	}

	private RestHighLevelClient buildClient() {
		try {
			LOGGER.info("\n");
			LOGGER.info("----------------building REST HIGHT LEVEL CLIENT------------");
			
			
			if(getenv("ELK_USERNAME") != null){	
				LOGGER.info("--Reading ELK details fom system env...");
				datasourceProperties.setElkpassword(getenv("ELK_PASSWORD").trim());
				datasourceProperties.setElkusername(getenv("ELK_USERNAME").trim()); 
			} 
			
			LOGGER.info("---ELK CONNCECTION---" + datasourceProperties.toString());

			SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();
			SSLContextBuilder builder2 = SSLContexts.custom();
			builder2.loadTrustMaterial(null, new TrustStrategy() {

				@Override
				public boolean isTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
					return true;
				}
			});
			
			SSLContext sslContext = builder2.build();
			credentialsProvider.setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(datasourceProperties.getElkusername(), datasourceProperties.getElkpassword()));

			RestClientBuilder builder = RestClient
					.builder(new HttpHost(datasourceProperties.getElkurl(),
							Integer.parseInt(datasourceProperties.getElkport()), "https"))
					.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
						@Override
						public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
							httpClientBuilder.setSSLContext(sslContext);
							httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
							return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
						}
					});

			restHighLevelClient = new RestHighLevelClient(builder);
			RestClient restClient = restHighLevelClient.getLowLevelClient();

			NodesSniffer nodesSniffer = new ElasticsearchNodesSniffer(restClient, TimeUnit.SECONDS.toMillis(8),
					ElasticsearchNodesSniffer.Scheme.HTTPS);

			sniffer = Sniffer.builder(restClient).setNodesSniffer(nodesSniffer).build();
			sniffOnFailureListener.setSniffer(sniffer);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return restHighLevelClient;
	}

}
