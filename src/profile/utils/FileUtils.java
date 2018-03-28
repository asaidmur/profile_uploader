package profile.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;




public class FileUtils 
{

	private static Logger logger = LogManager.getLogger(FileUtils.class);
	
	
	public static String readFile(String path, Charset encoding) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(path));
			return new String(encoded, encoding);
		} catch (IOException ioe) {
			logger.error("file error:" + ioe.getMessage());
			return "";
		}
	}
	
}
