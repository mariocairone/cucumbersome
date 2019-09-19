package com.mariocairone.cucumbersome.steps.database;

import org.testcontainers.containers.JdbcDatabaseContainer;

import com.mariocairone.cucumbersome.config.AbstractModuleConfig;

public class DatabaseConfig extends AbstractModuleConfig {

	private static DatabaseConfig instance = new DatabaseConfig();

	protected static final String DB_URL = "database.url";
	protected static final String DB_USERNAME = "database.username";
	protected static final String DB_PASSWORD = "database.password";

	private String databaseUrl;
	private String databaseUsername;
	private String databasePassword;;

	private DatabaseConfig() {
		super();
	}

	public static DatabaseConfig databaseOptions() {
		return instance;
	}

	public void withDatabaseContainer(JdbcDatabaseContainer<?> container) {
		withDatabaseUrl(container.getJdbcUrl());
		withDatabaseUsername(container.getUsername());
		withDatabasePassword(container.getPassword());
	}

	@Override
	protected void loadProperties() {

		if (getSettings().isDefined(DB_USERNAME))
			this.databaseUsername = getSettings().get(DB_USERNAME, String.class);
		if (getSettings().isDefined(DB_PASSWORD))
			this.databasePassword = getSettings().get(DB_PASSWORD, String.class);
		if (getSettings().isDefined(DB_URL))
			this.databaseUrl = getSettings().get(DB_URL, String.class);
	}

	public DatabaseConfig withDatabaseUrl(String databaseUrl) {
		this.databaseUrl = databaseUrl;
		return this;
	}

	public DatabaseConfig withDatabaseUsername(String databaseUsername) {
		this.databaseUsername = databaseUsername;
		return this;
	}

	public DatabaseConfig withDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
		return this;
	}

	protected String getDatabaseUrl() {
		return databaseUrl;
	}

	protected String getDatabaseUsername() {
		return databaseUsername;
	}

	protected String getDatabasePassword() {
		return databasePassword;
	}

}
