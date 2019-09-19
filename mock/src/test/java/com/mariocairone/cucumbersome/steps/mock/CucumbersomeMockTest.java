package com.mariocairone.cucumbersome.steps.mock;


import static com.mariocairone.cucumbersome.steps.mock.MockConfig.mockOptions;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MockServerContainer;

import com.mariocairone.cucumbersome.steps.http.HttpConfig;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber",
									  "json:target/cucumber/cucumber.json", 
									  "junit:target/cucumber/cucumber.xml"	}, 
    glue = {"com.mariocairone.cucumbersome.steps"},
    features = "classpath:features/mock",
    strict = true)
public class CucumbersomeMockTest {
	
	
	private static MockServerContainer mockServer = new MockServerContainer();			
			
	 @BeforeClass
	  public static void init() {
		 
		 mockServer.start();
		 
		 mockOptions()
		 		.addService("default",mockServer);
		 
		 HttpConfig.httpOptions().withPort(mockServer.getServerPort());
		 

		

	 }
	

}
