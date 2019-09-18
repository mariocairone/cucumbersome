package com.mariocairone.cucumbersome.settings;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.mariocairone.cucumbersome.template.parser.TemplateParser;


public class Settings {

	private static Settings instance;
		
	public static final String CONFIGURATION_FILE_NAME = "cucumbersome.properties";
		
	private  Properties properties;

	private  Map<String,Object> globalVariables;
	
	private TemplateParser parser;

	public static Settings getInstance() {
		if(instance == null)
			instance = new Settings();
		
		return instance;
	}

	private Settings() {
		
		this.globalVariables = new HashMap<>();
		this.parser = new TemplateParser(globalVariables); 
		this.properties = readProperties(CONFIGURATION_FILE_NAME);
			    	
	}
	
	private Properties readProperties(String fileName) {
		
		Properties prop = new Properties();
    	InputStream input = null;
    	
    	try {
        
    		input = this.getClass().getClassLoader().getResourceAsStream(fileName);
    		if(input==null){
    				System.out.println("File not found: " + fileName);
    		    return new Properties();
    		}

    		//load a properties file from class path, inside static method
    		 prop.load(input);

    	} catch (IOException ex) {
    		ex.printStackTrace();
        } finally{
        	if(input!=null){
        		try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	}
        }
		return prop;		
	}
	
	public Properties getProperties() {
		return properties;
	}

	public Map<String, Object> getGlobalVariables() {
		return globalVariables;
	}
	

	public boolean isDefined(String name) {
		return properties.containsKey(name);
	}	
	
	public <T> T getOrDefault(String name, T defaultValue,Class<T> type) {
		Object property = get(name, type);
		if(property == null)
			return defaultValue;	
		return type.cast(property);
	}
	
	public <T> T get(String name, Class<T> type) {
		String property = properties.getProperty(name);
		if(property == null)
			return null;
		
		
			property = parser.parse(property);
		
		
		PropertyEditor editor = PropertyEditorManager.findEditor(type);
		editor.setAsText(property);
		return type.cast(editor.getValue());
	}
	
	
    public Set<String> getKeysStartingWith(final String headerPrefix) {
		Collection<String> keys = properties.stringPropertyNames();
        return keys.stream().filter(s -> s.startsWith(headerPrefix)).collect(Collectors.toSet());   
    }
	
}
