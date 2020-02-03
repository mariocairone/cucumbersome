package com.mariocairone.cucumbersome.steps.mock.container;

import org.testcontainers.containers.GenericContainer;

public class MockServiceContainer extends GenericContainer<MockServiceContainer> {

	@java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MockServiceContainer.class);
    public static final String VERSION = "5.5.4";
    public static final int PORT = 1080;
    
    public MockServiceContainer() {
        this(VERSION);
    }
    
    public MockServiceContainer(String version) {
        super("jamesdbloom/mockserver:mockserver-" + version);
        withCommand("-logLevel INFO -serverPort " + PORT);
        addExposedPorts(PORT);
    }
    
    public MockServiceContainer(Integer port) {
    	this(VERSION,port);
    }

    public MockServiceContainer(String version, Integer port) {
    	this(version);
        addFixedExposedPort(port, PORT);
    }
          
    public String getEndpoint() {
        return String.format("http://%s:%d", getContainerIpAddress(), getMappedPort(PORT));
    }

    public Integer getServerPort() {
        return getMappedPort(PORT);
    }
}