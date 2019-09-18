package com.mariocairone.cucumbersome.steps.http;

import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.options;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.trace;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mariocairone.cucumbersome.settings.Settings;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber", 
		"json:target/cucumber/cucumber.json",
		"junit:target/cucumber/cucumber.xml" }, 
		glue = {	"com.mariocairone.cucumbersome.steps" }, 
		features = "classpath:features/http", 
		strict = true)
public class CucumbersomHttpIT  {

	static final Map<String, Object> variables = Settings.getInstance().getGlobalVariables();

	
	private static WireMockRule wm = 
			new WireMockRule(options().dynamicPort().portNumber());

	@BeforeClass
	public static void init() {


		wm.start();

		Integer port = wm.port();
		variables.put("wiremockPort",port );
		
		HttpConfig.httpOptions().withPort(port);

		
		
		wm.stubFor(get("/").willReturn(ok().withHeader("Content-Type", "application/json"))

		);

		wm.stubFor(head(urlEqualTo("/")).willReturn(ok())

		);

		wm.stubFor(options(urlEqualTo("/")).willReturn(ok().withHeader("Content-Type", "application/json"))

		);

		wm.stubFor(trace(urlEqualTo("/")).willReturn(ok().withHeader("Content-Type", "application/json"))

		);

		wm.stubFor(delete(urlEqualTo("/")).willReturn(ok())

		);
		wm.stubFor(post("/withData").withRequestBody(equalToJson("{\"test\": \"test\"}"))
				.willReturn(okJson("{\"foo\": \"bar\"}")));
		wm.stubFor(post("/withFormParam").willReturn(okJson("{\"foo\": \"bar\"}")));

		wm.stubFor(patch(urlEqualTo("/withData")).withRequestBody(equalToJson("{\"test\": \"test\"}"))
				.willReturn(okJson("{\"foo\": \"bar\"}")));

		wm.stubFor(put("/withData").withRequestBody(equalToJson("{\"test\": \"test\"}"))
				.willReturn(okJson("{\"foo\": \"bar\"}")));

		wm.stubFor(post("/withData").withRequestBody(equalToJson("[{\"test\": \"test\"}]"))
				.willReturn(okJson("{\"foo\": \"bar\"}")));

		wm.stubFor(get(urlPathEqualTo("/withParams")).withQueryParam("param", equalTo("paramValue"))
				.willReturn(okJson("[]")));

		wm.stubFor(get("/withHeaders").withHeader("MyHeader", equalTo("MyHeaderValue"))
				.willReturn(aResponse().withHeader("Content-Type", "application/json")
						.withHeader("MyHeader", "MyHeaderValue")
						.withHeader("MultiHeader", "MultiHeaderValue1", "MultiHeaderValue2")));

		wm.stubFor(get("/withArray")
				.willReturn(okJson("[{\"foo\": \"bar\"}, {\"foo\": 3}, {\"foos\": [\"bar\", \"wee\"]}]")));

		wm.stubFor(get("/emptyArray").willReturn(okJson("[]")));

		wm.stubFor(get("/response").willReturn(okJson("{\"foo\": \"bar\"}")));

		wm.stubFor(get("/response/bar").willReturn(okJson("{\"bar\": \"wee\"}")));

		wm.stubFor(post("/withFile").withMultipartRequestBody(aMultipart().withName("file")

		).willReturn(ok().withHeader("Content-Type", "text/plain")));
	}
}
