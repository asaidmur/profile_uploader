package profile.utils;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import profile.config.Configuration;

public class JaxbUtils {
	
	private static Logger logger = LogManager.getLogger(JaxbUtils.class);

	
	public static Configuration getConfigurationFromFile(File file)
	{
		Configuration config  = null;
	
		try
		{
            JAXBContext jc = JAXBContext.newInstance(Configuration.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			config = (Configuration) unmarshaller.unmarshal(file);			
		}
		catch(Exception e)
		{
			logger.error("Error reading settings file: " + e);
		}
		
		return config;
	}

}
