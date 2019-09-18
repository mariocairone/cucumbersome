package com.addisonlee.cucumber.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.function.DoubleFunction;

import org.junit.Test;

import com.mariocairone.cucumbersome.template.parser.TemplateParser;

import io.marioslab.basis.template.Error.TemplateException;

public class TemplateParserTest {

	
	@Test
	public void testTemplateParserWithStringTemplate() throws Exception {
		TemplateParser parser = new TemplateParser();
		parser.getGlobalVariables().put("myVar", "value1");
		
		
		String result = parser.parse("{{ myVar }}");
		assertEquals(result, "value1");
		
	}
	
	@Test
	public void testTemplateParserWithIncludedTemplate() throws Exception {
		TemplateParser parser = new TemplateParser();
		parser.getGlobalVariables().put("myVar", "value1");
		
		String result = parser.parse("{{ include \"template.bt\" }}");

		assertEquals(result, "value1");
		
	}
	
	@Test(expected = TemplateException.class)
	public void testTemplateParserWithIncludedTemplatenotExisting() throws Exception {
		TemplateParser parser = new TemplateParser();
		
		parser.parse("{{ include \"template-not-exist.bt\" }}");
		
	}
	
	
	@Test
	public void testTemplateParserWithClassFunction() throws Exception {
		TemplateParser parser = new TemplateParser();
		parser.getGlobalVariables().put("cos", (DoubleFunction<Double>)Math::cos);
		
		 String result = parser.parse("{{ cos(3.14) }}");
		 assertNotNull(result);
	}
	
	@Test
	public void testTemplateParserReadFileFunction() throws Exception {
		TemplateParser parser = new TemplateParser();
		parser.getGlobalVariables().put("myVar", "value1");
		
		 String result = parser.parse("{{ readFile(\"file.txt\") }}");
		 assertEquals("test",result);
	}
	
	
}
