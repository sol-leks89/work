    import javax.jms.*;
    import javax.naming.Context;
    import javax.naming.InitialContext;

import java.io.InputStream;
import java.util.*;


public class qpidReader {
		public boolean StartConnection()
		{
			boolean retCd = false;
			Properties properties = new Properties();
	          
			try {
				InputStream is = this.getClass().getResourceAsStream("/hello.properties");
				System.out.println("Post stream");
				
				if (is == null)
						System.out.println("Aaack - is is null");
				properties.load(is);  
				System.out.println("Post load");
				
	    
			    Context context = new InitialContext(properties); 
			    System.out.println("Post Init");
			
			    ConnectionFactory connectionFactory
			    = (ConnectionFactory) context.lookup("qpidConnectionfactory"); 
			    
			    Connection connection = connectionFactory.createConnection();  
			}
			catch (Exception e) {
				System.out.println("qpidReaderException!");
				System.out.println(e.getMessage());
				e.printStackTrace();
				System.out.println(e);
			}
			
			return retCd;
		}


}
