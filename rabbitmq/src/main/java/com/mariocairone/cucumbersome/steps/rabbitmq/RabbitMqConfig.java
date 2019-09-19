package com.mariocairone.cucumbersome.steps.rabbitmq;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.RabbitMQContainer;

import com.mariocairone.cucumbersome.config.AbstractModuleConfig;

public class RabbitMqConfig extends AbstractModuleConfig {
    private final Logger logger = LoggerFactory.getLogger(RabbitMqConfig.class);

	
	private static RabbitMqConfig instance = new RabbitMqConfig();

	protected static final String RABBITMQ_HOST = "rabbitmq.host";
	protected static final String RABBITMQ_PORT = "rabbitmq.port";
	protected static final String RABBITMQ_USERNAME = "rabbitmq.username";
	protected static final String RABBITMQ_PASSWORD = "rabbitmq.password";
	protected static final String RABBITMQ_VIRTUAL_HOST = "rabbitmq.virtualHost";
	protected static final String RABBITMQ_USE_SSL = "rabbitmq.ssl";
	protected static final String RABBITMQ_DEFAULT_EXCHANGE = "rabbitmq.default.exchange";
	protected static final String RABBITMQ_DEFAULT_READ_TIMEOUT = "rabbitmq.default.read.timeout";

	private String rabbitHost;
	private Integer rabbitPort;
	private String rabbitUsername;
	private String rabbitPassword;
	private String rabbitVirtualHost;
	private Boolean useSsl;
	private String rabbitDefaultExchange;
	private Integer rabbitDefaultReadTimeout = 5;
	private List<RabbitExchangeConfig> exchanges = new ArrayList<>();
	private String amqpUrl;
	
	private RabbitMQContainer rabbitMqContainer;
	
	private RabbitMqConfig() {
		super();	
	}

	public static RabbitMqConfig rabbitMqOptions() {
		return instance;
	}

	protected String getRabbitHost() {
		return rabbitHost;
	}

	public RabbitMqConfig withRabbitHost(String rabbitHost) {
		this.rabbitHost = rabbitHost;
		return rabbitMqOptions();
	}

	protected Integer getRabbitPort() {
		return rabbitPort;
	}

	public RabbitMqConfig withRabbitPort(Integer rabbitPort) {
		this.rabbitPort = rabbitPort;
		return rabbitMqOptions();
	}

	protected String getRabbitUsername() {
		return rabbitUsername;
	}

	public RabbitMqConfig withRabbitUsername(String rabbitUsername) {
		this.rabbitUsername = rabbitUsername;
		return rabbitMqOptions();
	}

	protected String getRabbitPassword() {
		return rabbitPassword;
	}

	public RabbitMqConfig withRabbitPassword(String rabbitPassword) {
		this.rabbitPassword = rabbitPassword;
		return rabbitMqOptions();
	}

	protected String getRabbitVirtualHost() {
		return rabbitVirtualHost;
	}

	public RabbitMqConfig withRabbitVirtualHost(String rabbitVirtualHost) {
		this.rabbitVirtualHost = rabbitVirtualHost;
		return rabbitMqOptions();
	}

	protected Boolean getUseSsl() {
		return useSsl;
	}

	public RabbitMqConfig withUseSsl(Boolean useSsl) {
		this.useSsl = useSsl;
		return rabbitMqOptions();
	}

	protected String getRabbitDefaultExchange() {
		return rabbitDefaultExchange;
	}

	public RabbitMqConfig withRabbitDefaultExchange(String rabbitDefaultExchange) {
		this.rabbitDefaultExchange = rabbitDefaultExchange;
		return rabbitMqOptions();
	}

	protected Integer getRabbitDefaultReadTimeout() {
		return rabbitDefaultReadTimeout;
	}

	public RabbitMqConfig withRabbitDefaultReadTimeout(Integer rabbitDefaultReadTimeout) {
		this.rabbitDefaultReadTimeout = rabbitDefaultReadTimeout;
		return rabbitMqOptions();
	}

	protected List<RabbitExchangeConfig> getExchanges() {
		return exchanges;
	}

	public RabbitMqConfig withExchanges(List<RabbitExchangeConfig> exchanges) {
		this.exchanges = exchanges;
		return rabbitMqOptions();
	}

	protected RabbitMQContainer getRabbitMqContainer() {
		return rabbitMqContainer;
	}
	
	protected String getAmqpUrl() {
		return this.amqpUrl;
	}
	
	public void withRabbitMqContainer(RabbitMQContainer rabbitMqContainer) {
		this.amqpUrl = rabbitMqContainer.getAmqpUrl();
	}
	


	@Override
	public void loadProperties() {

		if (getSettings().isDefined(RABBITMQ_HOST))
			withRabbitHost(getSettings().get(RABBITMQ_HOST, String.class));
		if (getSettings().isDefined(RABBITMQ_PORT))
			withRabbitPort(getSettings().get(RABBITMQ_PORT,Integer.class));
		if (getSettings().isDefined(RABBITMQ_USERNAME))
			withRabbitUsername(getSettings().get(RABBITMQ_USERNAME,String.class));
		if (getSettings().isDefined(RABBITMQ_PASSWORD))
			withRabbitPassword(getSettings().get(RABBITMQ_PASSWORD,String.class));
		if (getSettings().isDefined(RABBITMQ_VIRTUAL_HOST))
			withRabbitVirtualHost(getSettings().get(RABBITMQ_VIRTUAL_HOST,String.class));
		if (getSettings().isDefined(RABBITMQ_USE_SSL))
			withUseSsl(getSettings().get(RABBITMQ_USE_SSL,Boolean.class));
		if (getSettings().isDefined(RABBITMQ_DEFAULT_EXCHANGE))
			this.rabbitDefaultExchange = getSettings().get(RABBITMQ_DEFAULT_EXCHANGE,String.class);
		if (getSettings().isDefined(RABBITMQ_DEFAULT_READ_TIMEOUT))
			this.rabbitDefaultReadTimeout = getSettings().get(RABBITMQ_DEFAULT_READ_TIMEOUT,Integer.class);

		this.exchanges = getExchangesFromConfigFile();
		
	}

	private List<RabbitExchangeConfig> getExchangesFromConfigFile()  {

		List<RabbitExchangeConfig> configExchanges = new ArrayList<>();

		Pattern propertyPattern = Pattern.compile("rabbitmq.exchange\\.(\\d+)\\.(name|type|durable)");
		Set<String> keys = new TreeSet<>(getSettings().getKeysStartingWith("rabbitmq.exchange"));

		Set<String> ids = keys.stream().map(propertyPattern::matcher).filter(Matcher::matches)
				.map(matcher -> matcher.group(1)).collect(Collectors.toSet());

		for (String id : ids) {

			String name = null;
			if (getSettings().isDefined("rabbitmq.exchange." + id + ".name")) {
				name = getSettings().get("rabbitmq.exchange." + id + ".name",String.class);
			} else {
				logger.error("No name specified for predefined exchange: rabbitmq.exchange.%s.name",id);
			}
			String type = getSettings().getOrDefault("rabbitmq.exchange." + id + ".type", "direct",String.class);

			boolean durable = getSettings().getOrDefault("rabbitmq.exchange." + id + ".durable", false, Boolean.class);
			configExchanges.add(new RabbitExchangeConfig(name, type, durable));
		}

		return configExchanges;
	}


	
	

}
