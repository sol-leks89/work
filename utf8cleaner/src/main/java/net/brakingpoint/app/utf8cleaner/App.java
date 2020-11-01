package net.brakingpoint.app.utf8cleaner;
import org.apache.log4j.PropertyConfigurator;

import org.apache.log4j.Logger;
import net.brakingpoint.tools.utilities.*;

/**
 * Hello world!
 *
 */
public class App 
{
    static final Logger logger = Logger.getLogger(App.class);

    public static void main( String[] args )
    {
        fileutilities f = new fileutilities();

        PropertyConfigurator.configure("log4j.properties");
        logger.trace("*** Starting UTF8Cleaner");


        System.out.println( "Starting UTF8CLEANER!" );
        
        if (args.length != 1) {
            System.out.println("Udsage: utf8cleaner <filespec>");
                logger.error("incorrect filespec parameter");
        }
        if (f.RemoveAltChars(args[0]) < 0) {
            logger.error("Could not process file");
        }
    }
}
