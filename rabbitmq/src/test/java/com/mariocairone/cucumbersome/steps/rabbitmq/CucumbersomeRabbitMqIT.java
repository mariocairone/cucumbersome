package com.mariocairone.cucumbersome.steps.rabbitmq;


import static com.mariocairone.cucumbersome.steps.rabbitmq.RabbitMqConfig.rabbitMqOptions;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.mariocairone.cucumbersome.settings.Settings;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;


@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber",
									  "json:target/cucumber/cucumber.json", 
									  "junit:target/cucumber/cucumber.xml"	}, 
    glue = {"com.mariocairone.cucumbersome.steps"},
    features = "classpath:features/rabbitmq",
    strict = true)
public class CucumbersomeRabbitMqIT {

	static final Map<String, Object> variables = Settings.getInstance().getGlobalVariables();

	
	private static RabbitMQContainer rabbitMq = new RabbitMQContainer()				
            .withExposedPorts(5672)
            .withUser("mule", "password")
            .withVhost("/")
            .waitingFor(Wait.forListeningPort());

	
	 @BeforeClass
	  public static void init() {
		 rabbitMq.start();
		 rabbitMqOptions()
		 		.withRabbitMqContainer(rabbitMq);
		 
		 variables.put("rabbitMqContainerIp",rabbitMq.getContainerIpAddress());
		 variables.put("rabbitMqContainerPort",rabbitMq.getFirstMappedPort());
			 	

	 }
	 

}
