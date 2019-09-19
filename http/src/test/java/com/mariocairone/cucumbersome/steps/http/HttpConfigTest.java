package com.mariocairone.cucumbersome.steps.http;

import static com.mariocairone.cucumbersome.steps.http.HttpConfig.httpOptions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

public class HttpConfigTest {

	@Test
	public void testConfig() throws Exception {
		Boolean relaxedHttps= true;
		Integer port = 8080;
		String noContentPlaceholder = "-";
		Boolean logResponse = true;
		Boolean logRequest = true;
		String baseUrl = "http://localhost";
		String basePath= "/";
		HttpConfig config = httpOptions()
								.withRelaxedHttps(relaxedHttps)
								.withPort(port)
								.withNoContentPlaceholder(noContentPlaceholder)
								.withLogResponse(logResponse)
								.withLogRequest(logRequest)
								.withBaseUri(baseUrl)
								.withBasePath(basePath);
		
		assertEquals(config.getBasePath(),basePath);
		assertTrue(config.getRelaxedHttps());
		assertTrue(config.getLogRequest());
		assertTrue(config.getLogResponse());
		assertEquals(config.getBaseUrl(),baseUrl);
		assertEquals(config.getPort(),port);
		assertEquals(config.getNoContentPlaceholder(),noContentPlaceholder);
	}
	
	@After
	public void reloadConfig() {
		HttpConfig.httpOptions().loadProperties();
	}
}
