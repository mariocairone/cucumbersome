package com.mariocairone.cucumbersome.steps.mock;


import static com.mariocairone.cucumbersome.steps.mock.config.MockConfig.mockOptions;
import com.mariocairone.cucumbersome.steps.mock.container.MockServiceContainer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.mariocairone.cucumbersome.steps.http.HttpConfig;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber",
									  "json:target/cucumber/cucumber.json", 
									  "junit:target/cucumber/cucumber.xml"	}, 
    glue = {"com.mariocairone.cucumbersome.steps"},
    features = "classpath:features/mock",
    tags = "@test",
    strict = true)
public class CucumbersomeMockTest {
	
	
	private static MockServiceContainer mockServer = new MockServiceContainer();			
			
	 @BeforeClass
	  public static void init() {
		 
		 mockServer.start();
		 
		 mockOptions()
		 		.addService("default",mockServer);
		 
		 HttpConfig.httpOptions().withPort(mockServer.getServerPort());
		 
	 }
	

	 @AfterClass
	 public static void  after() {
	//	 System.out.println(mockServer.getLogs());
	 }
	 
}
