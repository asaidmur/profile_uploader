package profile.uploader;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;


import profile.utils.FileUtils;
import profile.utils.JaxbUtils;
import profile.config.Configuration;
import profile.config.Profile;
import profile.constants.ConfigConstants;
import profile.constants.EsConstants;
import profile.dao.ProfileDao;





public class ProfileUploader extends ProfileDao
{

	private static Logger logger = LogManager.getLogger(ProfileUploader.class);
	
	
	public static void main(String[] args) throws ParseException, OpenXML4JException, XmlException 
	{
		// commons cli setup
		
		Options options = new Options();
		options.addOption(ConfigConstants.CONFIG, true, "config");
	
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		
		
		
		// get config option value
		
		String config = cmd.getOptionValue(ConfigConstants.CONFIG);

		if(config == null) 
		{
			logger.error("ERROR: no params.Expecting settings (xml) file");
			return;
		}



		

		
		// process profile config
		
		ProfileUploader uploader = new ProfileUploader();
		uploader.process(config);
		
		
	}
	
	
	
	public void process(String cfgPath) throws OpenXML4JException, XmlException 
	{
		
		// get jaxb config
		
		File cfgFile = new File(cfgPath);
		
		Configuration cfg = JaxbUtils.getConfigurationFromFile(cfgFile);
		
		

		// get profiles
		
		List<Profile> profiles = cfg.getProfiles();

		
		//  iterate through profiles

		Iterator<Profile> profileIterator = profiles.iterator();
		
		while (profileIterator.hasNext()) 
		{
			Profile profile = profileIterator.next();
			processProfile(profile);
		}
		
		
	}
	
	
	private static void processProfile(Profile profile) throws OpenXML4JException, XmlException 
	{
	
		// get profile keys
		
		String[] keys = (String[]) profile.getKeys().getKeyFields().toArray(new String[0]);

		
		// get csv map list
		
		try 
		{
			
			// get profile path
			
			processCsv(profile.getPath(), keys);
		
			logger.info("map processed");
		} 
		catch (IOException ex) 
		{
			logger.info(ex.getMessage());
		}
		
		
		
	}
	
	
	

	private static void processCsv(String csvFilePath, String[] keys) throws FileNotFoundException, IOException 
	{

		BufferedReader br = new BufferedReader(new FileReader(csvFilePath));

		// Reading header.
		String line = br.readLine();
		String[] titles = line.split(",");

		// Reading body
		while ((line = br.readLine()) != null) 
		{
			if (!line.trim().isEmpty()) 
			{
				processCsvRow(titles, line, keys);
			}
		}
		
		br.close();
		
		//return mapList;
	}

	
	
	private static void processCsvRow(String[] titles, String line, String[] keys) 
	{

		String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

		Map<String, String> csvMap = new HashMap<String, String>();
		
		// Generate key-value pairs
		for (int i = 0; i < fields.length; i++) 
		{
			csvMap.put(titles[i], fields[i]);
			if (titles[i].contains(EsConstants.MSG_SEQ)) {
				csvMap.put(titles[i], String.valueOf((System.currentTimeMillis())));
			}
		}

		
		
		// get key key
	
		Map<String,String> keyMap = getKeyMap(keys, csvMap);

		
		// get doc keys
		
		String delimittedKey = getDelimitedKey(keyMap);
		int hashedKey = getHashedKey(delimittedKey);	
		
		csvMap.put(EsConstants.HASHED_KEY, String.valueOf(hashedKey));
		csvMap.put(EsConstants.DELIMITTED_KEY, delimittedKey);
		
		
		
		
		// save csv map
		try 
		{
			ProfileDao.saveProfileDoc(csvMap);
		} 
		catch (InterruptedException | ExecutionException e) 
		{
			e.printStackTrace();
		}
		

	}
	
	private static int getHashedKey(String[] keys, Map <String,String> csvMap) 
	{

		Map<String,String> keyMap = getKeyMap(keys, csvMap);
		int i = getHashedKey(keyMap);

		return i;
	}
	
	

	
	private static Map<String, String> getKeyMap(String[] keys, Map <String,String> csvMap) 
	{

		Map<String,String> keyMap = new TreeMap<String, String>();

		//Map<String, String> keyMap = new HashMap<String, String>();

		for (String key : keys) 
		{
			keyMap.put(key, (String) csvMap.get(key));
		}
		
		logger.debug(keyMap);

		return keyMap;
	}
	
	
	

	
	private static int getHashedKey(Map<String,String> m)
	{
		String s = getDelimitedKey(m);
		
		int i = s.hashCode();
		
		return i;
	}

	
	private static int getHashedKey(String s)
	{
		int i = s.hashCode();
		
		return i;
	}
	
	private static String getDelimitedKey(Map<String,String> m)
	{

		StringBuilder sb = new StringBuilder();
		
		Iterator<Map.Entry<String, String>> mEntries = m.entrySet().iterator();
		
		while (mEntries.hasNext()) 
		{
		    Map.Entry<String, String> mEntry = mEntries.next();
		
		    sb.append(mEntry.getKey());
		    sb.append(":");
		    sb.append(mEntry.getValue());
		    sb.append("|");
		    
		    logger.debug("Key = " + mEntry.getKey() + ", Value = " + mEntry.getValue());
		}

		String s = sb.substring(0, sb.length() -1);

		return s;
	}
	
	

}