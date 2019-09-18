package com.mariocairone.cucumbersome.template.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.marioslab.basis.template.Template;
import io.marioslab.basis.template.TemplateContext;
import io.marioslab.basis.template.TemplateLoader.MapTemplateLoader;
import io.marioslab.basis.template.TemplateLoader.Source;
import io.marioslab.basis.template.parsing.Ast.Include;
import io.marioslab.basis.template.parsing.Parser;

public class TemplateParser {

	private Map<String, Object> globalVariables;

	private Map<String, Object> variables;
	
	private TemplateContext context;

	/**
	 * Create a parser to be injected by PicoContainer
	 */
	public TemplateParser() {
		super();
		this.variables = new HashMap<>();
		this.context = new TemplateContext();
		context.set("readFile", (Function<String,String> ) this::readFile);
		this.globalVariables = new HashMap<>(); 
	}

	/**
	 * Create a parser to be used without DI
	 */
	public TemplateParser(Map<String, Object> globalVariables) {
		super();
		this.variables = new HashMap<>();
		this.context = new TemplateContext();
		context.set("readFile", (Function<String,String> ) this::readFile);
		
		this.globalVariables = globalVariables;
	}
	
	public String parse(String input)  {
		if (input == null)
			return null;

		if (input.isEmpty())
			return input;

		MapTemplateLoader loader = new MapTemplateLoader();
		loader.set("main", input);

		processInclusions(input, loader);
		
		variables.putAll(globalVariables); 
		variables
				.forEach((k, v) -> context.set(k, v));
		
		Template template = loader.load("main");

		return template.render(context);

	}

	private void processInclusions(String input, MapTemplateLoader loader) {
		List<Include> includes = new Parser().parse(new Source("input", input)).getIncludes();
		for (Include include : includes) {

			String path = include.getPath().getText().replaceAll("^.|.$", "");


			String template = readFile(path);


			if (template == null)
				return;

			loader.set(path, template);
			processInclusions(template, loader);

		}
	}

	private String readFile(String path) {
		
		if(path == null || path.isEmpty())
			return null;
		
		String content = null;
			
		try {
			content = getResourceFileAsString(path);		
		} catch (Exception e) {
			return null;
		}
		
		return content;
	}

	public Map<String, String> parse(Map<String, String> input) throws IOException {
		final Map<String, String> parsed = new HashMap<>();

		input.forEach((k,v) -> {
			parsed.put(parse(k), parse(v));
		});
		
		return parsed;

	}

	public List<List<String>> parse(List<List<String>> input) throws IOException {
		List<List<String>> rows = new ArrayList<>();
		for (List<String> row : input) {
			List<String> columns = new ArrayList<>();
			for (String col : row) {
				columns.add(parse(col));
			}
			rows.add(columns);
		}

		return rows;

	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	public Map<String, Object> getGlobalVariables() {
		return globalVariables;
	}

	public void setGlobalVariables(Map<String, Object> globalVariables) {
		this.globalVariables = globalVariables;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}
	
	
	private String getResourceFileAsString(String fileName) throws IOException {
	    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	    try (InputStream is = classLoader.getResourceAsStream(fileName)) {
	        if (is == null) return null;
	        try (InputStreamReader isr = new InputStreamReader(is);
	             BufferedReader reader = new BufferedReader(isr)) {
	            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
	        }
	    }
	}
}
