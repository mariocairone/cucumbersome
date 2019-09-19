package com.mariocairone.cucumbersome.template.aspect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.mariocairone.cucumbersome.template.aspect.ParseArgsAspect;
import com.mariocairone.cucumbersome.template.parser.TemplateParser;

public class ParseArgsAspectTest {

	protected TemplateParser parser = new TemplateParser();
	
	@Test
	public void testTemplateParserReflection() throws Exception {
		
		ParseArgsAspect aspect = new ParseArgsAspect();
		
		TemplateParser foundParser = aspect.getTemplateParser(this);
	
		assertNotNull(parser);
		assertEquals(parser, foundParser);
		
	}
	
	
}
