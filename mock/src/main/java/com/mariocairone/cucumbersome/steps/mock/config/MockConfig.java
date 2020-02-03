package com.mariocairone.cucumbersome.steps.mock;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.Network;

import com.mariocairone.cucumbersome.config.AbstractModuleConfig;

public class MockConfig extends AbstractModuleConfig {

	private static MockConfig instance = new MockConfig();

	protected final static String MOCK_SERVICES_PREFIX = "mock.services";
	protected final static String MOCK_SERVICE_HOST_SUFFIX = ".host";
	protected final static String MOCK_SERVICE_PORT_SUFFIX = ".port";
	

	private  Map<String, MockServerContainer> containers;	
	private  Map<String, MockServerClient> services;
		
	private MockConfig() {
		super();
	}

	public static MockConfig mockOptions() {
		return instance;
	}
	
	protected Map<String, MockServerContainer> getContainers() {
		return containers;
	}

	protected Map<String, MockServerClient> getServices() {
		return services;
	}

	@Override
	protected void loadProperties(){
		if(containers == null)
			containers = new ConcurrentHashMap<>();
		if(services == null)
			services = new ConcurrentHashMap<>();
				
		List<String> serviceNames = null;

		if (getSettings().isDefined(MOCK_SERVICES_PREFIX)) {
			String servicesProperty = getSettings().get(MOCK_SERVICES_PREFIX, String.class);
			serviceNames = Arrays.asList(servicesProperty.split(","));
		} else {
			serviceNames = new ArrayList<>();
			serviceNames.add("default");
		}

		for (String serviceName : serviceNames) {
			String host = serviceName;
			Integer port = null;
			MockServerContainer container = new MockServerContainer();
			
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
	

	public Integer getServicePort(String serviceName, Integer originalPort) {
		 MockServerContainer  container = containers.get(serviceName);
				if(container == null)
					return null;
		
		return container.getMappedPort(originalPort);
				
	}	
	
	public MockConfig addService(String serviceName,MockServerContainer container) {			
		
		containers.put(serviceName, container);		
		String host = "localhost" ;	
		Integer port = 1080;
		try {
			URL url = new URL(container.getEndpoint());
			host = url.getHost();	
			port = url.getPort();
		} catch (MalformedURLException e) {}
		
		MockServerClient client = new MockServerClient(host, port);
		services.put(serviceName, client);
		return mockOptions();
	}
	

	public void clearServices() {
		services.clear();
		containers.clear();
	}
}
	

	
