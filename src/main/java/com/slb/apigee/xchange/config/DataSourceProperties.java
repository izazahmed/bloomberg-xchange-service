package com.slb.apigee.xchange.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sims.datasource")
public class DataSourceProperties {

	private String elkurl;
	private String elkport;
	private String elkusername;
	private String elkpassword;

	public String getElkurl() {
		return elkurl;
	}

	public void setElkurl(String elkurl) {
		this.elkurl = elkurl;
	}

	public String getElkport() {
		return elkport;
	}

	public void setElkport(String elkport) {
		this.elkport = elkport;
	}

	public String getElkusername() {
		return elkusername;
	}

	public void setElkusername(String elkusername) {
		this.elkusername = elkusername;
	}

	public String getElkpassword() {
		return elkpassword;
	}

	public void setElkpassword(String elkpassword) {
		this.elkpassword = elkpassword;
	}

	@Override
	public String toString() {
		return "DataSourceProperties [elkurl=" + elkurl + ", elkport=" + elkport + ", elkusername=" + elkusername +"]";
	}

	 
	

}
