package com.goeuro.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesUtils {

	final static String TEST_PROPERTIES = "\\goeuro.properties";
	final static Logger logger = Logger.getLogger(PropertiesUtils.class);
	
	public static String getProperty(String key) {

		Properties properties = new Properties();
		String result = "";
		try {
			properties.load(new BufferedReader(new FileReader((new File("."))
					.getCanonicalPath() + TEST_PROPERTIES)));
			result = properties.getProperty(key);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("Property '" + key + "' with value '" + result + "' is retreived from file '" + TEST_PROPERTIES + "'");
		return result;
	}

}
