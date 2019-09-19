package com.mariocairone.cucumbersome.steps;

import java.util.Map;

import com.mariocairone.cucumbersome.settings.Settings;
import com.mariocairone.cucumbersome.template.parser.TemplateParser;

public class BaseStepDefs {

	protected Settings settings = Settings.getInstance();

	protected Map<String, Object> globalVariables;

	protected Map<String, Object> variables;

	protected TemplateParser parser;

	public BaseStepDefs(TemplateParser parser) {
		super();
		parser.setGlobalVariables(settings.getGlobalVariables());
		this.parser = parser;
		this.globalVariables = parser.getGlobalVariables();
		this.variables = parser.getVariables();

	}

	public TemplateParser getParser() {
		return this.parser;
	}

	protected Settings getSettings() {
		return settings;
	}

	protected void setSettings(Settings settings) {
		this.settings = settings;
	}

	protected Map<String, Object> getGlobalVariables() {
		return globalVariables;
	}

	protected void setGlobalVariables(Map<String, Object> globalVariables) {
		this.globalVariables = globalVariables;
	}

	protected Map<String, Object> getVariables() {
		return variables;
	}

	protected void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	protected void setParser(TemplateParser parser) {
		this.parser = parser;
	}

}
