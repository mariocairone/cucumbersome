package com.mariocairone.cucumbersome.steps.variables;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mariocairone.cucumbersome.steps.BaseStepDefs;
import com.mariocairone.cucumbersome.template.aspect.ParseArgs;
import com.mariocairone.cucumbersome.template.parser.TemplateParser;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;

@SuppressWarnings("deprecation")
public class VariablesSteps extends BaseStepDefs {

    private final Logger logger = LoggerFactory.getLogger(VariablesSteps.class);

	public VariablesSteps(TemplateParser parser) {
		super(parser);
	}

	@ParseArgs
	@And("^the system property \"(.*)\" with value \"(.*)\"$")
	public void setSystemPropertyWithValue(String key, String value) {
		System.setProperty(key, value);
	}

	@ParseArgs
	@Given("^the system properties(?:[:])?$")
	public void setSystemPropertiesWithValues(Map<String, String> data) {
		data.forEach((k, v) -> System.setProperty(k, v));
	};

	@ParseArgs
	@Given("^the variable \"(.*)\" with value \"(.*)\"$")
	public void setVariableWithStringValue(String key, String value) {
		variables.put(key, value);
	};

	@ParseArgs
	@Given("^the variable \"(.*)\" with value (\\d+)$")
	public void setVariableWithIntegerValue(String key, Integer value) {
		variables.put(key, value);
	};

	@ParseArgs
	@Given("^the variables(?:[:])?$")
	public void setVariablesWithValues(Map<String, String> data) {
		variables.putAll(data);
	};

	@ParseArgs
	@Given("^the global variable \"(.*)\" with value \"(.*)\"$")
	public void setGlobalVariableWithStringValue(String key, String value) {
		globalVariables.put(key, value);
	};

	@ParseArgs
	@Given("^the global variable \"(.*)\" with value (\\d+)$")
	public void setGlobalVariableWithIntegerValue(String key, Integer value) {
		globalVariables.put(key, value);
	};

	@ParseArgs
	@Given("^the global variables(?:[:])?$")
	public void setGlobalVariablesWithValues(Map<String, String> data) {
		globalVariables.putAll(data);
	};

	@ParseArgs
	@Given("^the logger print \"(.*)\"$")
	public void theLoggerPrint(String data) {
		logger.info(data);
	}


}
