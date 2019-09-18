package com.mariocairone.cucumbersome.steps.database;


import static com.mariocairone.cucumbersome.steps.database.DatabaseConfig.databaseOptions;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.mariocairone.cucumbersome.settings.Settings;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;





@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber",
									  "json:target/cucumber/cucumber.json", 
									  "junit:target/cucumber/cucumber.xml"	}, 
    glue = {"com.mariocairone.cucumbersome.steps"},
    features = "classpath:features/db",
    strict = true)
public class CucumbersomeDatabaseIT {
	
	static final Map<String, Object> variables = Settings.getInstance().getGlobalVariables();

	private static  PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>()		
			.withInitScript("sample-data.sql")
			.withUsername("mule")
			.withPassword("password")
			.waitingFor(Wait.forListeningPort());	
	
	 @BeforeClass
	  public static void setup() {

		 postgres.start();
		 
		 databaseOptions()
		 	.withDatabaseContainer(postgres);

		 
		 variables.put("postgres",postgres);
	 }
	 
}
