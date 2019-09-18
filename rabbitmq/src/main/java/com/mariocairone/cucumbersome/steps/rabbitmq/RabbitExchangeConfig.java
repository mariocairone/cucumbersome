package com.mariocairone.cucumbersome.steps.rabbitmq;

public class RabbitExchangeConfig {
	
	private String name;
	private String type;
	private Boolean durable;
	
	public RabbitExchangeConfig(String name, String type, Boolean durable) {
		super();
		this.name = name;
		this.type = type;
		this.durable = durable;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getDurable() {
		return durable;
	}
	public void setDurable(Boolean durable) {
		this.durable = durable;
	}
	
	
}