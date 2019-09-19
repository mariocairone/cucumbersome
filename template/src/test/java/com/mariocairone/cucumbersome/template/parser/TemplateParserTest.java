package com.mariocairone.cucumbersome.template.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		assertEquals("value1",result );
		
	}
	
	@Test
	public void testTemplateParserWithIncludedTemplate() throws Exception {
		TemplateParser parser = new TemplateParser();
		parser.getGlobalVariables().put("myVar", "value1");
		
		String result = parser.parse("{{ include \"template.bt\" }}");

		assertEquals("value1",result);
		
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
	
	@Test
	public void testTemplateParserReadFileNullPath() throws Exception {
		TemplateParser parser = new TemplateParser();
		parser.getGlobalVariables().put("myVar", "value1");
		
		 String result = parser.parse("{{ readFile(\"\") }}");
		 assertEquals("",result);
	}
	
	@Test
	public void testTemplateParserParseEmptyString() throws Exception {
		TemplateParser parser = new TemplateParser();	
		 String result = parser.parse("");
		 assertEquals("",result);
	}
	
	@Test
	public void testTemplateParserParseNull() throws Exception {
	
		TemplateParser parser = new TemplateParser();	
		String input = null; 
		String result = parser.parse(input);
		assertNull(result);
	}
	
	@Test
	public void testTemplateParserGlobalVariables() throws Exception {
		TemplateParser parser = new TemplateParser();
		Map<String,Object> variables = new HashMap<>();
		variables.put("myVar", "value1");
		parser.setGlobalVariables(variables);
		
		
		 assertNotNull(parser.getGlobalVariables());
		 assertEquals(variables,parser.getGlobalVariables());
	}
	
	@Test
	public void testTemplateParserList() throws Exception {
		Map<String,Object> variables = new HashMap<>();
		variables.put("myVar", "value1");
		TemplateParser parser = new TemplateParser();
		parser.setGlobalVariables(variables);
		
		List<List<String>> list = new ArrayList<>();
		list.add(Arrays.asList("myVar", "{{ myVar }}"));

		List<List<String>> result = parser.parse(list);	
		
		 assertEquals("value1",result.get(0).get(1));

	}
	
	@Test
	public void testTemplateParserParseMap() throws Exception {
		Map<String,Object> variables = new HashMap<>();
		variables.put("myVar", "value1");
		TemplateParser parser = new TemplateParser(variables);

		Map<String,String> map = new HashMap<>();
		map.put("myVar", "{{ myVar }}");
		
		Map<String,String> result = parser.parse(map);
		
		
		 assertEquals("value1", result.get("myVar"));
	}
	
}
