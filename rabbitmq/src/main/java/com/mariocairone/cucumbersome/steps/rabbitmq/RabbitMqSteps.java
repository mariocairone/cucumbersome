package com.mariocairone.cucumbersome.steps.rabbitmq;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static com.mariocairone.cucumbersome.steps.rabbitmq.RabbitMqConfig.rabbitMqOptions;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import com.jayway.jsonpath.JsonPath;
import com.mariocairone.cucumbersome.config.ConfigurationException;
import com.mariocairone.cucumbersome.steps.BaseStepDefs;
import com.mariocairone.cucumbersome.template.aspect.ParseArgs;
import com.mariocairone.cucumbersome.template.parser.TemplateParser;
import com.mariocairone.cucumbersome.utils.AssertionUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.minidev.json.JSONArray;

@SuppressWarnings("deprecation")
public class RabbitMqSteps extends BaseStepDefs {

   
	private String rabbitDefaultExchange;
	private Integer rabbitDefaultReadTimeout;

	
	
	private ConnectionFactory factory;
	private Connection connection;

	private Channel channel;

	public MessageWrapper message;

	public RabbitMqSteps(TemplateParser parser) throws Exception {
		super(parser);
		factory = new ConnectionFactory();
		setRabbitHost(rabbitMqOptions().getRabbitHost());
		setRabbitPort(rabbitMqOptions().getRabbitPort());
		setRabbitVirtualHost(rabbitMqOptions().getRabbitVirtualHost());
		setRabbitUsername(rabbitMqOptions().getRabbitUsername());
		setRabbitPassword(rabbitMqOptions().getRabbitPassword());
		setUseSsl(rabbitMqOptions().getUseSsl());
		setRabbitDefaultExchange(rabbitMqOptions().getRabbitDefaultExchange());
		setRabbitDefaultReadTimeout(rabbitMqOptions().getRabbitDefaultReadTimeout());
		setExchanges(rabbitMqOptions().getExchanges());
		setRabbitAmqpUrl(rabbitMqOptions().getAmqpUrl());
	}

	public void sendMessage(String exchange, String routingKey, MessageWrapper message) throws Exception {
		channel.basicPublish(exchange, routingKey, message.getProperties().build(), message.getBody().getBytes());
	}

	public MessageWrapper receiveMessage(String queue, int timeoutInSeconds) throws Exception {
		BlockingQueue<MessageWrapper> result = new ArrayBlockingQueue<MessageWrapper>(1);
		channel.basicConsume(queue, true, new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String response = new String(body);
				MessageWrapper messageWrapper = new MessageWrapper(response, properties);
				result.add(messageWrapper);
			}
		});
		return result.poll(timeoutInSeconds, TimeUnit.SECONDS);
	}

	public void waitForMessage(String queue, Optional<Integer> timeout) throws Exception {
		message = receiveMessage(queue, timeout.orElse(getTimeout()));
		assertThat(message, is(notNullValue()));
	}

	private Integer getTimeout() {
		return rabbitDefaultReadTimeout;
	}

	public void declareExchange(String exchange, String type, boolean durable) throws Exception, TimeoutException {
		getChannel().exchangeDeclare(exchange, type, durable);
	}

	public void declareQueue(String queueName, String exchange, String routingKey) throws Exception {
		 Channel channel = getChannel();

		boolean durable = false;
		boolean exclusive = false;
		boolean autoDelete = false;
		channel.queueDeclare(queueName, durable, exclusive, autoDelete, null);

		channel.queueBind(queueName, exchange, routingKey);
	}

	@Before
	public void before() {
		message = new MessageWrapper();
	}

	@After
	public void after() {
		invalidate();
	}

	@ParseArgs
	@Given("^the rabbitmq host is \"([^\"]*)\"$")
	public void configureRabbitMqHost(String host) throws IOException, TimeoutException {
		setRabbitHost(host);
	}

	@ParseArgs
	@Given("^the rabbitmq port is (\\d+)$")
	public void configureRabbitMqPort(Integer port) throws IOException, TimeoutException {
		setRabbitPort(port);
	}

	@ParseArgs
	@Given("^the rabbitmq port is \"([^\"]*)\"$")
	public void configureRabbitMqPortFromString(String port) throws IOException, TimeoutException {
		Integer portInt = Integer.parseInt(port);
		setRabbitPort(portInt);

	}

	@ParseArgs
	@Given("^the rabbitmq username is \"([^\"]*)\"$")
	public void configureRabbitMqUsername(String username) throws IOException, TimeoutException {
		setRabbitUsername(username);
	}

	@ParseArgs
	@Given("^the rabbitmq password is \"([^\"]*)\"$")
	public void configureRabbitMqPassword(String password) throws IOException, TimeoutException {
		setRabbitPassword(password);
	}

	@ParseArgs
	@Given("^the rabbitmq virtualHost is \"([^\"]*)\"$")
	public void setRabbitMqVirtualHost(String virtualHost) throws IOException, TimeoutException {
		setRabbitVirtualHost(virtualHost);
	}

	@ParseArgs
	@Given("^the rabbitmq default exhange is \"([^\"]*)\"$")
	public void configureRabbitMqDefaultExchange(String exchange) {
		rabbitDefaultExchange = exchange;
	}

	@Given("^the rabbitmq instance use SSL$")
	public void configureRabbitMqUseSSL() {
		setUseSsl(true);
	}

	@Given("^the rabbitmq instance doesn't use SSL$")
	public void setRabbitMqDoesntUseSSL() {
		setUseSsl(false);
	}

	@ParseArgs
	@Given("^the rabbitmq message body is:$")
	public void setRabbitMqMessageBody(String body) {
		message.setBody(body);
	}

	@ParseArgs
	@Given("^the rabbitmq message body is \"(.+)\"$")
	public void setRabbitMqMessageBodyInLine(String body) {
		message.setBody(body);
	}

	@ParseArgs
	@Given("^the rabbitmq message property reply-to is \"(.+)\"$")
	public void setRabbitMqReplayTo(String replyTo) {
		message.getProperties().replyTo(replyTo);
	}

	@ParseArgs
	@Given("^the rabbitmq message property content-type is \"(.+)\"$")
	public void setRabbitMqContentType(String contentType) {
		message.getProperties().contentType(contentType);
	}

	@ParseArgs
	@Given("^the rabbitmq queue \"([a-zA-Z0-9_\\-\\.:]+)\" is bind to exchange \"([a-zA-Z0-9_\\-\\.:]+)\" with routing key \"(.+)\"$")
	public void bindRabbitMqQueueToDirectExchangeWithRoutingKey(String queueName, String exchange, String routingKey)
			throws Exception {

		declareQueue(queueName, exchange, routingKey);
	}

	@ParseArgs
	@Given("^the rabbitmq queue \"([a-zA-Z0-9_\\-\\.:]+)\" is bind to exchange \"([a-zA-Z0-9_\\-\\.:]+)\"$")
	public void bindRabbitMqQueueToExchangeWithoutRoutingKey(String queueName, String exchange) throws Exception {
		declareQueue(queueName, exchange, "");
	}

	@ParseArgs
	@Given("^the rabbitmq queue \"([a-zA-Z0-9_\\-\\.:]+)\" is bind to default exchange with routing key \"(.+)\"$")
	public void bindRabbitMqQueueToDefaultExchangeWithRoutingKey(String queueName, String routingKey) throws Exception {

		declareQueue(queueName, rabbitDefaultExchange, routingKey);
	}

	@ParseArgs
	@When("^the rabbitmq( durable)? exchange \"([a-zA-Z0-9_\\-\\.:]+)\" is declared$")
	public void createRabbitMQExchange(String durable, String exchange) throws Exception {

		declareExchange(exchange, "direct", durable != null);
	}

	@ParseArgs
	@When("^the rabbitmq( durable)? exchange \"([a-zA-Z0-9_\\-\\.:]+)\" of type \"(topic|direct|fanout)\" is declared$")
	public void createRabbitMQExchangeWithType(String durable, String exchange, String type) throws Exception {

		declareExchange(exchange, type, durable != null);
	}

	@ParseArgs
	@When("^the rabbitmq client sends message with routing key \"(.+)\"$")
	public void sendRabbitMQMessageWithRoutingKey(String routingKey) throws Exception {

		sendMessage(rabbitDefaultExchange, routingKey, message);
	}

	@ParseArgs
	@When("^the rabbitmq client sends message to exchange \"([a-zA-Z0-9_\\-\\.:]+)\" with routing key \"(.+)\"$")
	public void sendRabbitMQMessageToExchangeWithRoutingKey(String exchange, String routingKey) throws Exception {
		sendMessage(exchange, routingKey, message);
	}

	@ParseArgs
	@When("^the rabbitmq client sends message to fanout exchange \"([a-zA-Z0-9_\\-\\.:]+)\"$")
	public void sendRabbitMQMessageToFanoutExchange(String exchange) throws Exception {
		sendMessage(exchange, "", message);
	}

	@ParseArgs
	@Then("^the rabbitmq message is received from the queue \"([a-zA-Z0-9_\\-\\.:]+)\"$")
	public void receiveRabbitMQMessageFromQueue(String queue) throws Exception {
		waitForMessage(queue, Optional.empty());
	}

	@ParseArgs
	@Then("^the rabbitmq message is received from the queue \"([a-zA-Z0-9_\\-\\.:]+)\" within (\\d+) seconds$")
	public void receiveRabbitMQMessageFromQueueWithinTime(String queue, Integer timeout) throws Exception {
		waitForMessage(queue, Optional.of(timeout));
	}

	@ParseArgs
	@Then("^the rabbitmq message entity \"([^\"]*)\" is stored in variable \"([^\"]*)\"$")
	public void storeRabbitMQMessageEntityInVariable(String entity, String key) throws Exception {
		Object value = JsonPath.compile(key).read(message.getBody());
		variables.put(key, value);
	}

	@ParseArgs
	@Then("^the rabbitmq message should be(?:[:])?$")
	public void assertRabbitMQMessage(String expectedBody) throws JSONException {
		String responseBody = message.getBody();
		JSONAssert.assertEquals(expectedBody, responseBody, false);
	}

	@Then("^the rabbitmq message body should be empty$")
	public void assertRabbitMQMessageBodyIsEmpty() {

		assertThat(message.getBody(), isEmptyString());
	}

	@Then("^the rabbitmq message body should not be empty$")
	public void assertRabbitMQMessageBodyIsntEmpty() {

		assertThat(message.getBody(), not(isEmptyString()));
	}

	@Then("^the rabbitmq message body should be an empty array$")
	public void assertRabbitMQMessageBodyIsEmptyArray() {

		JSONArray array = JsonPath.compile("$").read(message.getBody());
		assertThat(array.size(), is(0));
	}

	@ParseArgs
	@Then("^the rabbitmq message body should contain \"([^\"]*)\"$")
	public void assertRabbitMQMessageBodyContains(String jsonPath) {
		assertThat(message.getBody(), hasJsonPath(jsonPath));
	}

	@ParseArgs
	@Then("^the rabbitmq message body should not contain \"([^\"]*)\"$")
	public void assertRabbitMQMessageBodyNotContains(String jsonPath) {
		assertThat(message.getBody(), hasNoJsonPath(jsonPath));
	}

	@ParseArgs
	@Then("^the rabbitmq message body should contain \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void assertRabbitMQMessageBodyContainsWithValue(String jsonPath, String value) {
		assertThat(message.getBody(), hasJsonPath(jsonPath, equalTo(value)));

	}

	@ParseArgs
	@Then("^the rabbitmq message body should not contain \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void assertRabbitMQMessageBodyNotContainsWithValue(String jsonPath, String value) {
		assertThat(message.getBody(), hasJsonPath(jsonPath, not(equalTo(value))));
	}

	@ParseArgs
	@Then("^the rabbitmq message body should be an array with(?: (less than|more than|at least|at most))? (\\d+) element(?:s)?$")
	public void assertRabbitMQMessageBodyIsAnArrayWithElements(String comparisonAction, Integer count) {
		String body = message.getBody();
		final JSONArray jsonArray = JsonPath.compile("$").read(body);
		AssertionUtils.compareCounts(Optional.ofNullable(comparisonAction).orElse(""), count, jsonArray.size());

	}

	@ParseArgs
	@Then("^the rabbitmq message body should be an array with(?: (less than|more than|at least|at most))? \"([^\"]*)\" element(?:s)?$")
	public void assertRabbitMQMessageBodyIsAnArrayWithElements(String comparisonAction, String count) {

		Integer intCount = Integer.parseInt(count);

		String body = message.getBody();
		final JSONArray jsonArray = JsonPath.compile("$").read(body);
		AssertionUtils.compareCounts(Optional.ofNullable(comparisonAction).orElse(""), intCount, jsonArray.size());

	}

	@ParseArgs
	@Then("^the rabbitmq message body entity \"([^\"]*)\" should be(?:[:])?$")
	public void assertRabbitMQMessageBodyEntityIs(String jsonPath, String expectedJson) throws JSONException {

		String body = message.getBody();
		assertThat(body, hasJsonPath(jsonPath));

		Object responseEntity = JsonPath.compile(jsonPath).read(message.getBody());
		JSONAssert.assertEquals(expectedJson, responseEntity.toString(), false);

	}

	@ParseArgs
	@Then("^the rabbitmq message body entity \"([^\"]*)\" should be an array with(?: (less than|more than|at least|at most))? (\\d+) element(?:s)?$")
	public void assertRabbitMQMessageBodyEntityIsArrayWithElements(String entity, String comparisonAction,
			Integer count) throws JSONException {

		final JSONArray jsonArray = JsonPath.compile(entity).read(message.getBody());
		AssertionUtils.compareCounts(Optional.ofNullable(comparisonAction).orElse(""), count, jsonArray.size());

	}

	@ParseArgs
	@Then("^the rabbitmq message body entity \"([^\"]*)\" should be an array with(?: (less than|more than|at least|at most))? \"([^\"]*)\" element(?:s)?$")
	public void assertRabbitMQMessageBodyEntityIsArrayWithElements(String entity, String comparisonAction, String count)
			throws JSONException {

		Integer intCount = Integer.parseInt(count);

		final JSONArray jsonArray = JsonPath.compile(entity).read(message.getBody());
		AssertionUtils.compareCounts(Optional.ofNullable(comparisonAction).orElse(""), intCount, jsonArray.size());

	}

	@ParseArgs
	@Then("^the rabbitmq message headers should contain \"([^\"]*)\"$")
	public void assertRabbitMQMessageHeadersContains(String header) {
		assertNotNull(message.getHeaders().get(header));
	}

	@ParseArgs
	@Then("^the rabbitmq message headers should not contain \"([^\"]*)\"$")
	public void assertRabbitMQMessageHeadersNotContains(String header) {
		assertNull(message.getHeaders().get(header));
	}

	@ParseArgs
	@Then("^the rabbitmq message headers should contain \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void assertRabbitMQMessageHeadersContainsWithValue(String header, String value) {
		Object headerValue = message.getHeaders().get(header);
		assertEquals(value, headerValue);
	}

	@ParseArgs
	@Then("^the rabbitmq message headers should not contain \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void assertRabbitMQMessageHeadersNotContainsWithValue(String header, String value) {
		Object headerValue = message.getHeaders().get(header);
		assertNotEquals(value, headerValue);
	}

	protected void invalidate() {
		try {
			if (channel != null) {
				channel.close();
				channel = null;
			}
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (Exception e) {
			throw new ConfigurationException(e.getMessage(), e);

		}
	}

	private Connection getConnection() throws IOException, TimeoutException {

		if (connection == null) {
			connection = factory.newConnection();
		}
		return connection;
	}

	private Channel getChannel() throws IOException, TimeoutException {
		if (channel == null) {
			channel = getConnection().createChannel();
		}
		return channel;
	}

	private <T> void setConnectionFactoryParameter(Consumer<T> setter, T value) {
		if (value == null) {
			return;
		}
		invalidate();
		setter.accept(value);
	}

	private void setSsl(boolean ssl) {
		try {
			if (ssl) {
				invalidate();

				this.factory.useSslProtocol();

			}
		} catch (Exception e) {
			throw new RabbitMqStepsException(e.getMessage(),e);
		}
	}

	// **************************************
	// Setters
	// ***************************************

	private void setRabbitHost(String rabbitHost) {
		setConnectionFactoryParameter(factory::setHost, rabbitHost);
	}

	private void setRabbitPort(Integer rabbitPort) {
		setConnectionFactoryParameter(factory::setPort, rabbitPort);

	}

	private void setRabbitUsername(String rabbitUsername) {
		setConnectionFactoryParameter(factory::setUsername, rabbitUsername);
	}

	private void setRabbitPassword(String rabbitPassword) {
		setConnectionFactoryParameter(factory::setPassword, rabbitPassword);
	}

	protected void setRabbitVirtualHost(String rabbitVirtualHost) {
		setConnectionFactoryParameter(factory::setVirtualHost, rabbitVirtualHost);
	}

	protected void setUseSsl(Boolean useSsl) {
		setSsl(useSsl);
	}

	protected void setRabbitDefaultExchange(String rabbitDefaultExchange) {
		this.rabbitDefaultExchange = rabbitDefaultExchange;
	}

	protected void setRabbitAmqpUrl(String amqpUrl) {
		setConnectionFactoryParameter(t -> {
			try {
				factory.setUri(t);
			} catch (Exception e) { e.printStackTrace();}
		}, amqpUrl);
	}	
	
	protected void setRabbitDefaultReadTimeout(Integer rabbitDefaultReadTimeout) {
		this.rabbitDefaultReadTimeout = rabbitDefaultReadTimeout;
	}

	protected void setExchanges(List<RabbitExchangeConfig> exchanges) throws TimeoutException, Exception {
		for (RabbitExchangeConfig exchange : exchanges) {
			declareExchange(exchange.getName(), exchange.getType(), exchange.getDurable());
		}
	}
}
