package com.mariocairone.cucumbersome.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;
import java.util.Set;

import org.junit.Test;

public class SettingsTest {

	static {
		System.setProperty("test.env", "dev");
	}
	
	@Test
	public void getSettingsPropertiesTest() {
		
		Properties prop = Settings.getInstance().getProperties();
		
		assertNotNull(prop);
	}
	
	@Test
	public void getSettingsVariablesTest() {
		
		Object vars = Settings.getInstance().getGlobalVariables();
		
		assertNotNull(vars);
	}	
	
	
	@Test
	public void getSettingsBooleanPropertyTest() {
		
		Boolean prop = Settings.getInstance().get("rest.request.log",Boolean.class);
		
		assertTrue(prop);
	}	
	
	@Test
	public void getSettingsBooleanPropertyOrDefaultTest() {
		
		Boolean prop = Settings.getInstance().getOrDefault("non.existing.prop", false, Boolean.class);
		
		assertFalse(prop);
	}		
	

	@Test
	public void getSettingsStringPropertyTest() {
		
		String prop = Settings.getInstance().get("rest.request.baseUrl",String.class);
		
		assertNotNull(prop);
	}	
	

	@Test
	public void getSettingsStringPropertyOrDefaultNotExistingTest() {
		String defaultValue ="defaultValue";
		String prop = Settings.getInstance().getOrDefault("non.existing.prop", defaultValue, String.class);
		
		assertEquals(prop, defaultValue);
	}

	@Test
	public void getSettingsStringPropertyOrDefaultExistingTest() {
		String value ="http://localhost:8080";
		String prop = Settings.getInstance().getOrDefault("rest.request.baseUrl", "defaultValue", String.class);
		
		assertEquals(prop, value);
	}		
	
	@Test
	public void getSettingsisDefinedTest() {
		Boolean prop = Settings.getInstance().isDefined("non.existing.prop");
		
		assertFalse(prop);
		
		 prop = Settings.getInstance().isDefined("rest.request.baseUrl");

		assertTrue(prop);

	}		

	@Test
	public void getSettingsIntegerPropertyTest() {
		
		Integer prop = Settings.getInstance().get("rest.request.port", Integer.class);
		
		assertNotNull(prop);
	}

	
	
	@Test
	public void getSettingsKeyStartingWithTest() {
		
		Set<String> prop = Settings.getInstance().getKeysStartingWith("rest");
		
		assertNotNull(prop);
		assertFalse(prop.isEmpty());
	}	
	
	@Test
	public void loadSettingsPropertiesTest() {
		
		Properties prop = Settings.getInstance().getProperties();		
		assertNotNull(prop);
		assertFalse(prop.isEmpty());	
	}	
	
	@Test
	public void loadSettingsFileNotFoundTest() {
		String fileName = "not existing";
		Properties prop = Settings.getInstance().readProperties(fileName);		
		assertNotNull(prop);
		assertTrue(prop.isEmpty());
			
	}	
	

	
}
