package com.mariocairone.cucumbersome.steps;





import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.mariocairone.cucumbersome.settings.Settings;
import com.mariocairone.cucumbersome.steps.BaseStepDefs;
import com.mariocairone.cucumbersome.template.parser.TemplateParser;

public class BaseStepDefsTest {

	private BaseStepDefs stepClass;
	@Before
	public void setup() throws Exception {
		TemplateParser parser = new TemplateParser();
		stepClass =new BaseStepDefs(parser);
	}

	@Test
	public void testGlobalVariables() throws Exception {
		Map<String,Object> variables = new HashMap<>();
		stepClass.setGlobalVariables(variables);
		assertEquals(variables, stepClass.getGlobalVariables());
	}	
	
	@Test
	public void testVariables() throws Exception {
		Map<String,Object> variables = new HashMap<>();
		stepClass.setVariables(variables);
		assertEquals(variables, stepClass.getVariables());
	}		
	
	@Test
	public void testSettings() throws Exception {
		stepClass.setSettings(Settings.getInstance());
		assertEquals(Settings.getInstance(), stepClass.getSettings());
	}	

	@Test
	public void testParser() throws Exception {
		TemplateParser parser = new TemplateParser();
		stepClass.setParser(parser);
		assertEquals(parser, stepClass.getParser());
	}	
	
}
