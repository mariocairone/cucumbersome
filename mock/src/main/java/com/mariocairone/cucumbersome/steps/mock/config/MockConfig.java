package com.mariocairone.cucumbersome.steps.mock.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import com.mariocairone.cucumbersome.config.AbstractModuleConfig;
import com.mariocairone.cucumbersome.steps.mock.container.MockServiceContainer;

public class MockConfig extends AbstractModuleConfig {

	private static MockConfig instance = new MockConfig();

	protected final static String MOCK_SERVICES_PREFIX = "mock.services";
	protected final static String MOCK_SERVICE_HOST_SUFFIX = ".host";
	protected final static String MOCK_SERVICE_PORT_SUFFIX = ".port";
	protected final static String MOCK_SERVICE_FIXEDPORT_SUFFIX = ".fixedPort";
	
	private Map<String,Service> services;
		
	private MockConfig() {
		super();
	}

	public static MockConfig mockOptions() {
		return instance;
	}

	public Map<String, Service> getServices() {
		return services;
	}

	@Override
	protected void loadProperties(){

		if(services == null)
			services = new ConcurrentHashMap<>();
				
		List<String> serviceNames = new ArrayList<>();

		if (getSettings().isDefined(MOCK_SERVICES_PREFIX)) {
			String servicesProperty = getSettings().get(MOCK_SERVICES_PREFIX, String.class);
			serviceNames = Arrays.asList(servicesProperty.split(","));
		} 

		for (String serviceName : serviceNames) {
			String host = serviceName;
			Integer port = null;
			MockServiceContainer container = null;

			String fixedPortKey = String.format("%s.%s%s",
					MOCK_SERVICES_PREFIX,serviceName,MOCK_SERVICE_FIXEDPORT_SUFFIX);			
			
			if (getSettings().isDefined(fixedPortKey)) {
				Integer fixedPort = getSettings().get(fixedPortKey, Integer.class);
				container = new MockServiceContainer(fixedPort);
			} else {
				container = new MockServiceContainer();
			}
			
			String hostKey = String.format("%s.%s%s",
					MOCK_SERVICES_PREFIX,serviceName,MOCK_SERVICE_HOST_SUFFIX);
			
			if (getSettings().isDefined(hostKey)) {
				host = getSettings().get(hostKey, String.class);
				container.withNetworkAliases(host);
			} 
				
			String portKey = String.format("%s.%s%s",
						MOCK_SERVICES_PREFIX,serviceName,MOCK_SERVICE_PORT_SUFFIX);
				
			if (getSettings().isDefined(portKey)) {
				port = getSettings().get(portKey, Integer.class);
				container.withExposedPorts(port);
				container.withCommand("-logLevel INFO -serverPort " + port);
			}
			
			container.withNetwork(Network.SHARED);
			container.start();
			addService(serviceName, container);			
			
		}		
	}
	
	
	public MockConfig addService(String serviceName,GenericContainer<?> container) {			
		
		String host = container.getContainerIpAddress();	
		Integer port = container.getFirstMappedPort();
		
		Service service = new Service(host, port);
		services.put(serviceName, service);
		return mockOptions();
	}

	public MockConfig addService(String serviceName,String host, Integer port) {			
		
		Service service = new Service(host, port);
		services.put(serviceName, service);
		return mockOptions();
	}

	public void clearServices() {
		services.clear();
	}
}
	

	
