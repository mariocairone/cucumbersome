package com.mariocairone.cucumbersome.steps.mock;


import static com.mariocairone.cucumbersome.steps.mock.MockConfig.mockOptions;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MockServerContainer;

import com.mariocairone.cucumbersome.settings.Settings;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber",
									  "json:target/cucumber/cucumber.json", 
									  "junit:target/cucumber/cucumber.xml"	}, 
    glue = {"com.mariocairone.cucumbersome.steps"},
    features = "classpath:features/mock",
    strict = true)
public class CucumbersomeMockIT {
	
	static final Map<String, Object> variables = Settings.getInstance().getGlobalVariables();

	
	private static MockServerContainer mockServer = new MockServerContainer();			
			
	 @BeforeClass
	  public static void init() {
		 
		 mockServer.start();
		 
		 mockOptions()
		 		.addService("default",mockServer);
		 
		 variables.put("defaultServicePort", mockServer.getServerPort());
		

	 }
	

}
