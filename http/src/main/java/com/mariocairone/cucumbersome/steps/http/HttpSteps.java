package com.mariocairone.cucumbersome.steps.http;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static com.mariocairone.cucumbersome.steps.http.HttpConfig.httpOptions;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import org.hamcrest.Matchers;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import com.jayway.jsonpath.JsonPath;
import com.mariocairone.cucumbersome.steps.BaseStepDefs;
import com.mariocairone.cucumbersome.template.aspect.ParseArgs;
import com.mariocairone.cucumbersome.template.parser.TemplateParser;
import com.mariocairone.cucumbersome.utils.AssertionUtils;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.JSONArray;
@SuppressWarnings("deprecation")
public class HttpSteps extends BaseStepDefs {

	
	private Response response;
	private RequestSpecification request;


	private String noContentPlaceHolder;
	
	
	public HttpSteps(TemplateParser parser) {
		super(parser);
		
		this.request = given();
		request.baseUri(httpOptions().getBaseUrl());
		request.basePath(httpOptions().getBasePath());
		request.port(httpOptions().getPort());
		if(httpOptions().getRelaxedHttps())
			request.relaxedHTTPSValidation();		
		if(httpOptions().getLogRequest())
			request.filter(new RequestLoggingFilter());
		if(httpOptions().getLogResponse())		
			request.filter(new ResponseLoggingFilter());
		
		this.noContentPlaceHolder = httpOptions().getNoContentPlaceholder();
	}

	public void callAPI(String apiMethod, String path) {
			switch (apiMethod) {
		case "GET":
			response = request.get(path);
			break;
		case "PUT":
			response = request.put(path);
			break;
		case "POST":
			response = request.post(path);
			break;
		case "PATCH":
			response = request.patch(path);
			break;
		case "DELETE":
			response = request.delete(path);
			break;
		case "HEAD":
			response = request.head(path);
			break;
		case "OPTIONS":
			response = request.options(path);
			break;
		case "TRACE":
			response = request.request("TRACE", path);
			break;
        default:
        	throw new HttpStepsException(String.format("Unsupported HTTP method %s", apiMethod));
		}

	}
	
	@Before
	public void before() {
		
		response = null;		
	}
	
	@ParseArgs
	@Given("^the http request baseURI is \"([^\"]*)\"$")
	public void theBaseUriIs(String urlString) {
		request.baseUri(urlString);		
	}
	
	@ParseArgs
	@Given("^the http request basePath is \"([^\"]*)\"$")
	public void theBasePathIs(String path) {
		request.basePath(path);		
	}
	
	@ParseArgs	
	@Given("^the http request port is \"([^\"]*)\"$")
	public void thePortIs(String port) {
		request.port(Integer.parseInt(port));	
	}
	
	@ParseArgs	
	@Given("^the http request port is (\\d+)$")
	public void thePortIs(Integer port) {
		request.port(port);	
	}	
	
	@ParseArgs
	@Given("^the http request Content-Type is \"([^\"]*)\"$")
	public void theRequestContentTypeIs(String  contentType) {
		request.contentType(contentType);
	}
	
	@ParseArgs
	@Given("^the http request basic Authentication username is \"([^\"]*)\" and password is \"([^\"]*)\"$")
	public void theBasicAuthenticationIs( String  username, String password )  {

		request.auth().preemptive().basic(username, password);
	}	
	
	@ParseArgs
	@Given("^the http request OAuth2 Authentication token is \"([^\"]*)\"$")
	public void theBasicOAuth2Is( String  token)  {
		request.auth().preemptive().oauth2(token);
	}		
	
	@ParseArgs
	@Given("^the http request has Authorization header \"([^\"]*)\"$")
	public void theRequestAuthorizationHeaderIs( String  header)  {
		request.header("Authorization", header);
	}		
	
	@ParseArgs
	@Given("^the http request accept Content-Type \"([^\"]*)\"$")
	public void theRequestAcceptHeaderIs( String  contentType)  {
		request.accept(contentType);
	}		
	
	@ParseArgs
	@Given("^the http request form params are(?:[:])?$")
	public void theRequestFormParamsAre(Map<String, String> params)  {
		Map<String, String>  formParams = params.entrySet().stream().filter( entry -> !entry.getValue().trim().equals(noContentPlaceHolder))
				 .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));			 
	     request.formParams(formParams);
	}		
	
	@ParseArgs
	@Given("^the http request Cookies are(?:[:])?$")
	public void theRequestCookiesAre(Map<String, String> params)  {
		 
		Map<String, String> cookies = params.entrySet().stream().filter( entry -> !entry.getValue().endsWith(noContentPlaceHolder))
				 .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
		 
		 request.cookies(cookies);
	}
	
	
	@ParseArgs
	@Given("^the http request headers are(?:[:])?$")
	public void theRequestHeadersAre(Map<String, String> params)  {		 
		 params = params.entrySet().stream().filter( entry -> !entry.getValue().endsWith(noContentPlaceHolder))
				 .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
		 
		 request.headers(params);
	}
	
	@ParseArgs
	@Given("^the http request query params are(?:[:])?$")
	public void theRequestQueryParamsAre(Map<String, String> params)  {
		 
		Map<String, String>  queryparams = params.entrySet().stream().filter( entry -> !entry.getValue().endsWith(noContentPlaceHolder))
				 .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
		 
		 request.queryParams(queryparams);
	}	
	
	@ParseArgs
	@Given("^the http request body is(?:[:])?$")
	public void theRequestBodyIs( String data )  {
		 request.body(data);
	}	
	
	
	@ParseArgs
	@Given("^the http request (GET|POST|PUT|HEAD|DELETE|OPTIONS|PATCH|TRACE) \"([^\"]*)\" is executed$")
	public void theRequestIsExecuted(String method, String path)  {
		callAPI(method, path);
	}	
	
	@ParseArgs	
	@Given("^the http request (GET|POST|PUT|HEAD|DELETE|OPTIONS|PATCH|TRACE) \"([^\"]*)\" with following body is executed(?:[:])?$")
	public void theRequestIsExecutedWithBody(String method, String path, String body)   {

		request.body(body);

		callAPI(method, path);
	}

	
	@ParseArgs	
	@Given("^the http request (GET|POST|PUT|HEAD|DELETE|OPTIONS|PATCH|TRACE) \"([^\"]*)\" with multipart body file \"([^\"]*)\" from \"([^\"]*)\" is executed$")
	public void theRequestIsExecutedWithMultipartBody(String method, String path, String file,String from)   {

		request.multiPart(file, new File(HttpSteps.class.getResource("/").getPath() + from));
		callAPI(method, path);
	}	
	
	@ParseArgs
	@Then("^the http response status should be (\\d+)$")
	public void theResponseStatusShouldBe(Integer status)   {
		
		response.then().assertThat().statusCode(status);
	}	
	
	@ParseArgs	
	@Then("^the http response status should be \"([^\"]*)\"$")
	public void theResponseStatusShouldBeString(String status)   {
		
		Integer statusCode = Integer.parseInt(status);
		response.then().statusCode(statusCode);
	}	

	
	@ParseArgs	
	@Then("^the http response entity \"([^\"]*)\" is stored in variable \"([^\"]*)\"$")
	public void theResponseEntityIsStoredInVariable(String entity, String key)    {

		Object value = response.getBody().jsonPath().get(entity);

		variables.put(key, value);
	}	

	
	@ParseArgs	
	@Then("^the http response body should be(?:[:])?$")
	public void theResponseShouldBe(String expectedBody) throws JSONException     {
		String responseBody = response.getBody().asString();
		JSONAssert.assertEquals(expectedBody, responseBody, false);
	}
	
		
	@Then("^the http response body should be empty$")
	public void theResponseShouldBeEmpty()     {
		assertThat(response.getBody().asString(), isEmptyString());
	}	
	
	
	@Then("^the http response body should not be empty$")
	public void theResponseShouldNotBeEmpty()   {
		assertThat(response.getBody().asString(), not(isEmptyString()));
	}	
	
	
	@Then("^the http response body should be an empty array$")
	public void theResponseShouldNotBeEmptArray()    {
		response.then().contentType(ContentType.JSON).body("$", Matchers.hasSize(0));
	}		
	
	@ParseArgs
	@Then("^the http response body should contain \"([^\"]*)\"$")
	public void theResponseShouldContainJsonPath(String jsonPath)   {
		assertThat(response.getBody().asString(), hasJsonPath(jsonPath));	
	}		
	
	@ParseArgs	
	@Then("^the http response body should not contain \"([^\"]*)\"$")
	public void theResponseShouldNotContainJsonPath(String jsonPath)    {
		assertThat(response.getBody().asString(), hasNoJsonPath(jsonPath));
	}
	
	@ParseArgs
	@Then("^the http response body should contain \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void theResponseShouldContainJsonPathWithValue(String jsonPath, String value)     {
		assertThat(response.getBody().asString(), hasJsonPath(jsonPath, equalTo(value)));

	}	
	
	@ParseArgs
	@Then("^the http response body should not contain \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void theResponseShouldNotContainJsonPathWithValue(String jsonPath, String value)     {
		assertThat(response.getBody().asString(), hasJsonPath(jsonPath, not(equalTo(value))));
	}	
	
	@ParseArgs
	@Then("^the http response body should be an array with(?: (less than|more than|at least|at most))? (\\d+) element(?:s)?$")
	public void theResponseShouldBeAnArrayWithElements(String comparisonAction, Integer count)   {
		
		String body = response.getBody().asString();
		final JSONArray jsonArray = JsonPath.compile("$").read(body);
		AssertionUtils.compareCounts(Optional.ofNullable(comparisonAction).orElse(""), count, jsonArray.size());

	}		
	
	@ParseArgs
	@Then("^the http response body should be an array with(?: (less than|more than|at least|at most))? \"([^\"]*)\" element(?:s)?$")
	public void theResponseShouldBeAnArrayWithElements(String comparisonAction, String count)   {
		
		Integer intCount = Integer.parseInt(count);

		String body = response.getBody().asString();
		final JSONArray jsonArray = JsonPath.compile("$").read(body);
		AssertionUtils.compareCounts(Optional.ofNullable(comparisonAction).orElse(""), intCount, jsonArray.size());

	}		
	
	@ParseArgs
	@Then("^the http response body entity \"([^\"]*)\" should be(?:[:])?$")
	public void theResponseEntotyShouldBe(String jsonPath, String data) throws JSONException   {
		
		String responseEntity = response.getBody().jsonPath().getString(jsonPath);

		JSONAssert.assertEquals(data, responseEntity, false);

	}		
	
	@ParseArgs
	@Then("^the http response body entity \"([^\"]*)\" should be an array with(?: (less than|more than|at least|at most))? (\\d+) element(?:s)?$")
	public void theResponseEntotyShouldBeAnArrayWithElements(String entity, String comparisonAction, Integer count)  {
		
		String body = response.getBody().jsonPath().getString(entity);

		final JSONArray jsonArray = JsonPath.compile("$").read(body);
		AssertionUtils.compareCounts(Optional.ofNullable(comparisonAction).orElse(""), count, jsonArray.size());

	}	
	
	@ParseArgs
	@Then("^the http response body entity \"([^\"]*)\" should be an array with(?: (less than|more than|at least|at most))? \"([^\"]*)\" element(?:s)?$")
	public void theResponseEntotyShouldBeAnArrayWithElementsString(String entity, String comparisonAction, String count)  {
		Integer intCount = Integer.parseInt(count);

		String body = response.getBody().jsonPath().getString(entity);

		final JSONArray jsonArray = JsonPath.compile("$").read(body);
		AssertionUtils.compareCounts(Optional.ofNullable(comparisonAction).orElse(""), intCount, jsonArray.size());

	}		

	@ParseArgs
	@Then("^the http response time should be(?: (less than|more than|at least|at most))? (\\d+)(s|ms)$")
	public void theResponseTimeShouldBe(String comparisonAction, Integer time, String unit)  {
		TimeUnit timeUnit = TimeUnit.MILLISECONDS;
		
		if(unit.equals("s"))
			timeUnit = TimeUnit.SECONDS;
		

		Long responseTime = response.timeIn(timeUnit);

		AssertionUtils.compareCounts(Optional.ofNullable(comparisonAction).orElse(""), time, responseTime.intValue());

	}		
	
	@ParseArgs
	@Then("^the http response headers should contain \"([^\"]*)\"$")
	public void theResponseHeadersShouldContain(String header)  {
		assertNotNull(response.getHeader(header));

	}		
	
	@ParseArgs
	@Then("^the http response headers should not contain \"([^\"]*)\"$")
	public void theResponseHeadersShouldNotContain(String header)  {
		assertNull(response.getHeader(header));

	}		
	
	@ParseArgs
	@Then("^the http response headers should contain \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void theResponseHeadersShouldContainWithValue(String header, String value)   {
		List<Header> headers = response.getHeaders().getList(header);
		assertNotNull(headers);

		Optional<Header> responseHeader = headers.stream()
				.filter(entry -> entry.getValue().equals(value)).findFirst();

		assertTrue(responseHeader.isPresent());

	}	
	
	@ParseArgs
	@Then("^the http response headers should not contain \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void theResponseHeadersShouldNotContainWithValue(String header, String value)   {
		String responseHeaderValue = response.getHeader(header);
		assertNotNull(responseHeaderValue);
		assertNotEquals(responseHeaderValue, value);
	}		

		
}
