package dist.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

public class ConfigurationController {
	
	private Properties properties;
	
	public ConfigurationController (String configFilePath) throws FileNotFoundException {
		properties = new Properties();
		FileInputStream configFile = new FileInputStream(configFilePath);
		try {
			properties.loadFromXML(configFile);
		}
		catch (Exception e) {
			// ????????????
		}
	}
	
	public Properties getProperites() {
		return properties;
	}
	
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
}
