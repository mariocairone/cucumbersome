package com.mariocairone.cucumbersome.steps.database;

import org.testcontainers.containers.JdbcDatabaseContainer;

import com.mariocairone.cucumbersome.config.AbstractModuleConfig;
import com.mariocairone.cucumbersome.config.ConfigurationException;

public class DatabaseConfig extends AbstractModuleConfig {

	private static DatabaseConfig instance = new DatabaseConfig();

	protected final static String DB_URL = "db.url";
	protected final static String DB_USERNAME = "db.username";
	protected final static String DB_PASSWORD = "db.password";

	private String databaseUrl;
	private String databaseUsername;
	private String databasePassword;
//	private String databaseSchema;
//	private String databaseCatalog;

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
	protected void loadProperties() throws ConfigurationException {

		if (settings.isDefined(DB_USERNAME))
			this.databaseUsername = settings.get(DB_USERNAME, String.class);
		if (settings.isDefined(DB_PASSWORD))
			this.databasePassword = settings.get(DB_PASSWORD, String.class);
		if (settings.isDefined(DB_URL))
			this.databaseUrl = settings.get(DB_URL, String.class);
	}

	// **********************************
	// SETTERS
	// *********************************

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

//	public DatabaseConfig withDatabaseSchema(String databaseSchema) {
//		this.databaseSchema = databaseSchema;
//		return this;
//	}
//
//	public DatabaseConfig withDatabaseCatalog(String databaseCatalog) {
//		this.databaseCatalog = databaseCatalog;
//		return this;
//	}

	// **********************************
	// GETTERS
	// *********************************

	protected String getDatabaseUrl() {
		return databaseUrl;
	}

	protected String getDatabaseUsername() {
		return databaseUsername;
	}

	protected String getDatabasePassword() {
		return databasePassword;
	}

//	protected String getDatabaseSchema() {
//		return databaseSchema;
//	}
//
//	protected String getDatabaseCatalog() {
//		return databaseCatalog;
//	}

}
