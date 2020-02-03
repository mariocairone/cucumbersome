package com.mariocairone.cucumbersome.steps.mock;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.Not.*;
import static org.mockserver.model.JsonBody.json;
import static org.mockserver.model.JsonPathBody.jsonPath;
import static org.mockserver.model.XPathBody.xpath;
import static org.mockserver.model.XmlBody.xml;
import static org.mockserver.model.RegexBody.regex;
import static org.mockserver.model.StringBody.subString;
import static org.mockserver.model.Header.*;
import static org.mockserver.model.NottableString.*;
import static org.mockserver.model.Parameter.param;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.MatchType;
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;
import org.mockserver.model.Body;
import org.mockserver.model.Delay;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;
import org.mockserver.model.StringBody;

import com.mariocairone.cucumbersome.steps.BaseStepDefs;
import com.mariocairone.cucumbersome.steps.mock.config.MockConfig;
import com.mariocairone.cucumbersome.steps.mock.config.Service;
import com.mariocairone.cucumbersome.steps.mock.exceptions.MockStepsException;
import com.mariocairone.cucumbersome.template.aspect.ParseArgs;
import com.mariocairone.cucumbersome.template.parser.TemplateParser;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;





@SuppressWarnings("deprecation")
public class MockSteps extends BaseStepDefs {
			
	private MockServerClient client;
	private HttpRequest request;
	private HttpResponse response;

	private Map<String, Service> services;

	
	public MockSteps(TemplateParser parser) {
		super(parser);	
		
		 MockConfig config = MockConfig.mockOptions();
		 this.services = new HashMap<String, Service>(config.getServices());
		 
	}

	public void setRequestBody(Body<?> body) {
		request.withBody(body);
	}

	
	public MockServerClient getMockServerClient(String serviceName) {
		
		return services.computeIfAbsent(serviceName, key -> {
			String availableMockServices = services.keySet().stream().collect(Collectors.joining(", "));
			throw new MockStepsException("Unable to find http mock service by name:" + key + ". "
					+ "Available mock services are: {" + availableMockServices + "}");
		}).getClient();
	}	


	protected void finishHttpMock(Integer httpRespondStatusCode, Times times) {
		Expectation[] activeExpectations = client.retrieveActiveExpectations(request);
		Optional<Expectation> defaultExpectation = Arrays.stream(activeExpectations)
				.filter(exp -> exp.getTimes().equals(Times.unlimited())).findAny();

		client.clear(request);
		restoreNonDefaultExpectations(activeExpectations);

		response.withStatusCode(httpRespondStatusCode);
		client.when(request, times).respond(response);
		if (defaultExpectation.isPresent() && !times.equals(Times.unlimited())) {
			Expectation exp = defaultExpectation.get();
			restoreExpectation(exp);
		}

		request = null;
		response = null;
		client = null;
	}

	private void restoreNonDefaultExpectations(Expectation[] activeExpectations) {
		if (ArrayUtils.isNotEmpty(activeExpectations)) {
			Arrays.stream(activeExpectations).filter(exp -> !exp.getTimes().equals(Times.unlimited()))
					.forEach(this::restoreExpectation);
		}
	}

	private void restoreExpectation(Expectation exp) {
		client.when(exp.getHttpRequest(), exp.getTimes(), exp.getTimeToLive()).respond(exp.getHttpResponse());
	}
	
	@Before
	public void before() {
		services.forEach( (name,client) -> {
			client.getClient().reset();
		});
	}
	
	@ParseArgs	
	@Given("^the mock with path \"([^\"]*)\" and method \"([^\"]*)\" is added to service \"([^\"]*)\"$")
	public void theMockWithPathAndMethodIsAdded(String path, String httpMethod, String mockServiceName) {

		client = getMockServerClient(mockServiceName);
		request = request(path).withMethod(httpMethod);
		response = response();

			
	}


	@ParseArgs		
	@Given("^the mock receives a request with header \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void theMockReceiveARequestWithHeatherWithValue(String headerName, String headerValue) {
		request.withHeader(headerName, headerValue);
	}

	@ParseArgs		
	@Given("^the mock receives a request with header \"([^\"]*)\"$")
	public void theMockReceiveARequestWithHeather(String headerName) {
		request.withHeaders(header(headerName));
	}
	
	@ParseArgs		
	@Given("^the mock receives a request without header \"([^\"]*)\"$")
	public void theMockReceiveARequestWithoutHeather(String headerName) {
		request.withHeaders(header(not(headerName)));
	}	
	
	@ParseArgs		
	@Given("^the mock receives a request with query parameter \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void theMockReceiveARequestWithQueryParameterWithValue(String queryParameterName, String queryParameterValue) {

		request.withQueryStringParameter(param(queryParameterName, queryParameterValue));
	}	
	
	@ParseArgs	
	@Given("^the mock receives a request with query parameters(?:[:])?$")
	public void theMockReceiveARequestWithQueryParameters(Map<String, String> params) {
		 List<Parameter> parameters = params.entrySet().stream()
		 	.map( entry -> new Parameter(entry.getKey(),entry.getValue()))
		 .collect(Collectors.toList());
		 	
		request.withQueryStringParameters(parameters);
		
	}	
	
	@ParseArgs		
	@Given("^the mock receives a request with body equal to string(?:[:])?$")
	public void theMockReceiveARequestWithBody(String body) {
		this.setRequestBody(new StringBody(body));		
	}	
	
	@ParseArgs		
	@Given("^the mock receives a request with body containing the string \"(.*)\"$")
	public void theMockReceiveARequestWithBodyContainingString(String subString) {
		this.setRequestBody(subString(subString));		
	}	
	@ParseArgs		
	@Given("^the mock receives a request with body matching json(?:[:])?$")
	public void theMockReceiveARequestWithJsonBody(String body) {
		this.setRequestBody(json(body,MatchType.ONLY_MATCHING_FIELDS));
	}		
	
	@ParseArgs		
	@Given("^the mock receives a request with body matching the json path \"(.*)\"$")
	public void theMockReceiveARequestWithJsonPathBody(String jsonPath) {
		this.setRequestBody( jsonPath(jsonPath));
	}		
	
	@ParseArgs		
	@Given("^the mock receives a request with body not matching the json path \"(.*)\"$")
	public void theMockReceiveARequestWithoutJsonPathBody(String jsonPath) {
		this.setRequestBody(not(jsonPath(jsonPath)));
	}		
	
	@ParseArgs		
	@Given("^the mock receives a request with xml body(?:[:])?$")
	public void theMockReceiveARequestWithXmlBody(String body) {
		this.setRequestBody(xml(body));
	}		
	
	@ParseArgs		
	@Given("^the mock receives a request with body matching the xpath \"(.*)\"$")
	public void theMockReceiveARequestWithXPathBody(String xPath) {
		this.setRequestBody(xpath(xPath));
	}		
	
	@ParseArgs		
	@Given("^the mock receives a request with body not matching the xpath \"(.*)\"$")
	public void theMockReceiveARequestWithoutXPathBody(String format, String xPath) {
		this.setRequestBody(not(xpath(xPath)));
	}		
	
	@ParseArgs		
	@Given("^the mock receives a request with body matching regex \"(.*)\"$")
	public void theMockReceiveARequestWithregexBody(String regex) {
		this.setRequestBody(regex(regex));
	}	
	
	
	@ParseArgs
	@Given("^the mock response will have header \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void theMockResponseWillHaveHeaderWithValue(String headerName, String headerValue) {				 	
		response.withHeader(headerName, headerValue);
	}		
	
	@ParseArgs
	@Given("^the mock response will have body$")
	public void theMockResponseWillHaveBody(String responseBody) {			
		response.withBody(responseBody);
	}		
	
	@ParseArgs
	@Given("^the mock response will have body \"(.+)\"$")
	public void theMockResponseWillHaveBodyFromFile(String file) throws IOException {			 
		response.withBody(file);		
	}
	
	@ParseArgs
	@Given("^the mock response will have a delay of (\\d+)(s|ms)")
	public void theMockResponseWillHaveDelayOf(Integer time,String unit) {		
		 
		Delay delay =  null;
		
		if(unit.equals("s")) {
			delay =  new Delay(TimeUnit.SECONDS, time);
		} else {
			delay =  new Delay(TimeUnit.MILLISECONDS, time);
		}
		
		response.withDelay(delay);
		
	}	

	@ParseArgs
	@Given("^the mock responds with status code (\\d+)$")
	public void theMockResponseWillHaveStatusCode(Integer httpRespondStatusCode) {		
		finishHttpMock(httpRespondStatusCode, Times.unlimited());	
	}	

	
	@ParseArgs
	@Given("^the mock responds with status code \"([^\"]*)\" exactly (\\d+) times?$")
	public void theMockResponseWillHaveStatusCode(Integer httpRespondStatusCode, Integer times) {				 
		finishHttpMock(httpRespondStatusCode, Times.exactly(times));
	}	
	

		
}


