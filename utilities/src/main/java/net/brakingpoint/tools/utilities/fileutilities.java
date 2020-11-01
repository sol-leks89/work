package net.brakingpoint.tools.utilities;
import java.lang.Exception;
import java.lang.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public class fileutilities {

	static final Logger logger = Logger.getLogger(fileutilities.class);
	static final char replacementChar = '#';

	public  fileutilities()
	{
		PropertyConfigurator.configure("log4j.properties");
		logger.trace("FileUtilitieconstructor");
		
	}
	public static int RemoveAltChars(String filename) {
		int retCd = 0;
		
		logger.trace("entering RemoveAltChars");
		try {
			String extensionExtender = "clean";
			String outputPath = "./";
			
			List<String> records = new ArrayList<String>();
			String msg = "Opening " + filename;
			logger.trace(msg);
				BufferedReader reader = new BufferedReader(new FileReader(filename));
				String line;
				char[] chars;
				int i;
				
				while ((line = reader.readLine()) != null)
				{
					chars = line.toCharArray();
					for (i=0; i < chars.length; i++) {
						if (chars[i] > 127) {
							
							line.replace(chars[i], replacementChar);
							msg = "Replaced " 
							+ chars[i] 
							+ " at " 
							+ i 
							+ " at line " 
							+ records.size()+1;
							logger.info(msg);
						}
					}
					
					records.add(line);
				}
				reader.close();
				if (records.isEmpty() ) {
					msg = "No lines in file " + filename;
					logger.warn(msg);
				}
				else {
					msg = records.size()
					+ " lines processed.";
					logger.info(msg);
				}
				retCd = records.size();
			
		}
		catch (Exception e) {
			logger.error(e.getMessage());
			retCd = -1;
		}
		return (retCd);
	}
}