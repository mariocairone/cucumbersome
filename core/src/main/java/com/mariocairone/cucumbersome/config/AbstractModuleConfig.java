package com.mariocairone.cucumbersome.config;

import com.mariocairone.cucumbersome.settings.Settings;

public abstract class AbstractModuleConfig {

	protected Settings settings = Settings.getInstance();
	
	protected abstract void loadProperties() throws ConfigurationException;

	public AbstractModuleConfig() {
		super();
		loadProperties();
	}

	protected Settings getSettings() {
		return settings;
	}
	
}
