package com.mariocairone.cucumbersome.steps.rabbitmq;

public class RabbitMqStepsException extends RuntimeException{

	private static final long serialVersionUID = -8254626195744087625L;


	public RabbitMqStepsException(String message, Throwable cause) {
		super(message, cause);
	}

	public RabbitMqStepsException(String message) {
		super(message);
	}

	
	
	
}
