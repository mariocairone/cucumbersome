package com.mariocairone.cucumbersome.steps.database;

import static com.mariocairone.cucumbersome.steps.database.DatabaseConfig.databaseOptions;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

public class DatabaseConfigTest {

	@Test
	public void testBuilderMethods() throws Exception {
		String username = "mario";
		String password = "password";
		String url = "url";
		DatabaseConfig config =databaseOptions()
									.withDatabaseUsername(username)
									.withDatabasePassword(password)
									.withDatabaseUrl(url);
		
		assertEquals(config.getDatabaseUsername(), username);
		assertEquals(config.getDatabasePassword(), password);
		assertEquals(config.getDatabaseUrl(), url);
	}
	
	@After
	public void reloadConfig() {
		DatabaseConfig.databaseOptions().loadProperties();
	}	
}
