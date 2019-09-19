package com.mariocairone.cucumbersome.steps.rabbitmq;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

public class RabbitMqConfigTest {

	@Test
	public void testConfiguration() throws Exception {
		
		 String rabbitHost="localhost";
		 Integer rabbitPort=5672;
		 String rabbitUsername="mario";
		 String rabbitPassword="password";
		 String rabbitVirtualHost="mule";
		 Boolean useSsl=false;
		 String rabbitDefaultExchange="default";
		 Integer rabbitDefaultReadTimeout = 5;
		 List<RabbitExchangeConfig> exchanges = new ArrayList<>();
		 exchanges.add(new RabbitExchangeConfig("name", "direct", true));
		 
		 RabbitMqConfig config = RabbitMqConfig
		 							.rabbitMqOptions()
		 								.withExchanges(exchanges)
		 								.withRabbitDefaultExchange(rabbitDefaultExchange)
		 								.withRabbitDefaultReadTimeout(rabbitDefaultReadTimeout)
		 								.withRabbitHost(rabbitHost)
		 								.withRabbitPassword(rabbitPassword)
		 								.withRabbitPort(rabbitPort)
		 								.withRabbitUsername(rabbitUsername)
		 								.withUseSsl(useSsl)
		 								.withRabbitVirtualHost(rabbitVirtualHost);
		assertEquals(config.getExchanges(), exchanges);
		assertEquals(config.getRabbitDefaultExchange(), rabbitDefaultExchange);
		assertEquals(config.getRabbitDefaultReadTimeout(), rabbitDefaultReadTimeout);
		assertEquals(config.getRabbitHost(), rabbitHost);
		assertEquals(config.getRabbitPassword(), rabbitPassword);
		assertEquals(config.getRabbitPort(), rabbitPort);
		assertEquals(config.getRabbitUsername(), rabbitUsername);
		assertEquals(config.getUseSsl(), useSsl);
		assertEquals(config.getRabbitVirtualHost(), rabbitVirtualHost);
		
	}

	
	@After
	public void reloadConfig() {
		RabbitMqConfig.rabbitMqOptions().loadProperties();
	}
	
	
}
