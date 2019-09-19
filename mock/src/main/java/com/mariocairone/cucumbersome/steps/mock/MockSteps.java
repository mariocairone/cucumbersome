package com.mariocairone.cucumbersome.steps.mock;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

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
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;
import org.mockserver.model.Body;
import org.mockserver.model.Delay;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;
import org.mockserver.model.StringBody;

import com.mariocairone.cucumbersome.steps.BaseStepDefs;
import com.mariocairone.cucumbersome.template.aspect.ParseArgs;
import com.mariocairone.cucumbersome.template.parser.TemplateParser;

import cucumber.api.java.en.Given;





@SuppressWarnings("deprecation")
public class MockSteps extends BaseStepDefs {
			
	public MockServerClient client;
	public HttpRequest request;
	public HttpResponse response;

	private Map<String, MockServerClient> clients;

	
	public MockSteps(TemplateParser parser) {
		super(parser);	
		
		 MockConfig config = MockConfig.mockOptions();
		 this.clients = new HashMap<String, MockServerClient>(config.getServices());
		 
	}

	public void setRequestBody(Body<?> body) {
		request.withBody(body);
	}

	
	public MockServerClient getMockServerClient(String serviceName) {
		
		return clients.computeIfAbsent(serviceName, key -> {
			String availableMockServices = clients.keySet().stream().collect(Collectors.joining(", "));
			throw new MockStepsException("Unable to find http mock service by name:" + key + ". "
					+ "Available mock services are: {" + availableMockServices + "}");
		});
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
	
	@ParseArgs	
	@Given("^the mock with path \"([^\"]*)\" and method \"([^\"]*)\" is added to service \"([^\"]*)\"$")
	public void theMockWithPathAndMethodIsAdded(String path, String httpMethod, String mockServiceName) {

		client = getMockServerClient(mockServiceName);
		request = request(path).withMethod(httpMethod);
		response = response();

		client.when(request).respond(response);		
	}


	@ParseArgs		
	@Given("^the mock receive request with header \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void theMockReceiveARequestWithHeatherWithValue(String headerName, String headerValue) {

		request.withHeader(headerName, headerValue);
	}

	@ParseArgs		
	@Given("^the mock receive request with query parameter \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void theMockReceiveARequestWithQueryParameterWithValue(String queryParameterName, String queryParameterValue) {

		request.withQueryStringParameter(queryParameterName, queryParameterValue);
	}	
	
	@ParseArgs	
	@Given("^the mock receive request with query parameters(?:[:])?$")
	public void theMockReceiveARequestWithQueryParameters(Map<String, String> params) {
		 List<Parameter> parameters = params.entrySet().stream()
		 	.map( entry -> new Parameter(entry.getKey(),entry.getValue()))
		 .collect(Collectors.toList());
		 	
		request.withQueryStringParameters(parameters);
		
	}	
	
	@ParseArgs		
	@Given("^the mock receive request with body( from YAML|)(?:[:])?$")
	public void theMockReceiveARequestWithBody(String format, String body) {
		this.setRequestBody(new StringBody(body));		
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


