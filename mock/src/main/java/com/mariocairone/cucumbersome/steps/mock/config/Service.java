package com.mariocairone.cucumbersome.steps.mock.config;

import org.mockserver.client.MockServerClient;

public class Service {

	private String host;
	private Integer port;
	private MockServerClient client;

	public Service(String host, Integer port) {
		super();
		this.host = host;
		this.port = port;
		this.client = new MockServerClient(host, port);
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public MockServerClient getClient() {
		return client;
	}
	

}
