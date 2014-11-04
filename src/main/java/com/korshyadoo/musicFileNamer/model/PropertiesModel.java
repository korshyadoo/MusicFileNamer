package com.korshyadoo.musicFileNamer.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import com.korshyadoo.musicFileNamer.controller.ProgramLauncher;

public class PropertiesModel {
	private static PropertiesModel instance;
	private final Properties properties = new Properties();
	private static final Logger logger = ProgramLauncher.getLogger();
	public static final String VERSION_PROPERTY = "version";
	public static final String DEFAULT_DIRECTORY_PROPERTY = "defaultDirectory";
	public static final String MAIN_WINDOW_X_PROPERTY = "mainWindowX";
	public static final String MAIN_WINDOW_Y_PROPERTY = "mainWindowY";
	public static final String MAIN_WINDOW_WIDTH_PROPERTY = "mainWindowWidth";
	public static final String MAIN_WINDOW_HEIGHT_PROPERTY = "mainWindowHeight";
	private static final String PROPERTIES_FILE_LOCATION = "properties.properties";

	private PropertiesModel() throws FileNotFoundException, IOException {
		String propertyFileContents = readPropertyFileContents();
		properties.load(new StringReader(propertyFileContents.replace("\\", "\\\\")));
	}

	private String readPropertyFileContents() throws FileNotFoundException, IOException {
		InputStream fisTargetFile = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_LOCATION);
		String targetFileStr = IOUtils.toString(fisTargetFile, "UTF-8");
		return targetFileStr;
	}

	public static PropertiesModel getInstance() {
		if(instance == null) {
			try {
				instance = new PropertiesModel();
			} catch(IOException e) {
				logger.catching(e);
			}
		}

		return instance;
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}
}
