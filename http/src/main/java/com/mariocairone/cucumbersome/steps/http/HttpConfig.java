package com.mariocairone.cucumbersome.steps.http;

import com.mariocairone.cucumbersome.config.AbstractModuleConfig;
import com.mariocairone.cucumbersome.config.ConfigurationException;

public class HttpConfig extends AbstractModuleConfig {

	
	private static HttpConfig instance = new HttpConfig();
	
	protected static final String REST_BASE_URL = "rest.request.baseUrl";
	protected static final String REST_BASE_PATH = "rest.request.basePath";
	protected static final String REST_PORT = "rest.request.port";
	protected static final String REST_REQUEST_LOG = "rest.request.log";
	protected static final String REST_RESPONSE_LOG = "rest.response.log";
	protected static final String REST_RELAXED_HTTPS = "rest.relaxed.https";
	protected static final String REST_NO_CONTENT_PLACEHOLDER = "rest.nocontent.placeholder";
		
	private String baseUrl;
	private String basePath;
	private Integer port;
	private Boolean logRequest = false;
	private Boolean logResponse = false;
	private Boolean relaxedHttps = true;
	private String noContentPlaceholder;
	
	private HttpConfig() {
		super();			
	}
	
	public static HttpConfig httpOptions() {
		return instance;
	}
	
	protected String getBaseUrl() {
		return baseUrl;
	}
	public HttpConfig withBaseUri(String baseUrl) {
		this.baseUrl = baseUrl;
//		RestAssured.baseURI = baseUrl;
		return httpOptions();
	}
	protected String getBasePath() {
		return basePath;
	}
	public HttpConfig withBasePath(String basePath) {
		this.basePath = basePath;
//		RestAssured.basePath = basePath; 
		return httpOptions();
	}
	protected Integer getPort() {
		return port;
	}
	public HttpConfig withPort(Integer port) {
		this.port = port;
//		RestAssured.port = port;
		return httpOptions();
	}
	protected Boolean getLogRequest() {
		return logRequest;
	}
	public HttpConfig withLogRequest(Boolean logRequest) {
		this.logRequest = logRequest;
//		RestAssured.filters(new RequestLoggingFilter());
		return httpOptions();
	}
	protected Boolean getLogResponse() {
		return logResponse;
	}
	public HttpConfig withLogResponse(Boolean logResponse) {
		this.logResponse = logResponse;
//		RestAssured.filters(new ResponseLoggingFilter());
		return httpOptions();
	}
	protected Boolean getRelaxedHttps() {
		return relaxedHttps;
	}
	public HttpConfig withRelaxedHttps(Boolean relaxedHttps) {
		this.relaxedHttps = relaxedHttps;
//		RestAssured.useRelaxedHTTPSValidation();
		return httpOptions();

	}
	protected String getNoContentPlaceholder() {
		return noContentPlaceholder;
	}
	public HttpConfig withNoContentPlaceholder(String noContentPlaceholder) {
		this.noContentPlaceholder = noContentPlaceholder;
		return httpOptions();

	}

	@Override
	protected void loadProperties() throws ConfigurationException {
		          
		this.logRequest = settings.getOrDefault(REST_REQUEST_LOG, true, Boolean.class);
		this.logResponse = settings.getOrDefault(REST_RESPONSE_LOG, true, Boolean.class);
		this.relaxedHttps = settings.getOrDefault(REST_RELAXED_HTTPS, true, Boolean.class);
		this.baseUrl = settings.getOrDefault(REST_BASE_URL, "http://localhost",String.class);
		this.basePath =settings.getOrDefault(REST_BASE_PATH,"/",String.class);
		this.port =settings.getOrDefault(REST_PORT,8080,Integer.class );
    	this.noContentPlaceholder = settings.getOrDefault(REST_NO_CONTENT_PLACEHOLDER, "-",String.class);
	
	}

	
	
	
	
	
}
