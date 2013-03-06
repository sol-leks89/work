
/*************************************************************************
 *
 * iJet CONFIDENTIAL
 * __________________
 *
 *  2012 iJet Onboard Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of iJet Onboard Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to iJet Onboard Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from iJet Onboard Incorporated.
 */

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.naming.Context;
import javax.naming.InitialContext;




/**
 * This class is used to create new AMQP sessions. It uses the same connection for 
 * all sessions. Each session is meant to connect to a different topic exchange.
 */
public class SessionFactory {
	
	private static final String CONNECTION_FACTORY = "qpidConnectionfactory";
	protected Connection connection;

    // public static Logger logger = LoggerFactory.getLogger(SessionFactory.class) ;
	
	/**
	 * Starts a new JMS/AMQP session given a JNDI URL.
	 * @param jndiUrl JNDI URL
	 */
	public SessionFactory(String jndiUrl) {
		Properties contextProps = new Properties();
		contextProps.put("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
		contextProps.put("connectionfactory.qpidConnectionfactory", jndiUrl);
			System.out.println("Post try");
		try {
			Context context = new InitialContext(contextProps);
			ConnectionFactory inputConnectionFactory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY);
		    System.out.println("Got a Factory");
			connection = inputConnectionFactory.createConnection();
		    connection.start();
		} catch (Exception e) {
            /*if (logger.isErrorEnabled()) logger.error("Unable to connect to the Qpid broker @ " + jndiUrl, e);
             * 
		*/
			System.out.println("SessionFactory Constructor exception!");
			System.out.println(e.getMessage());
		}
        //if (logger.isDebugEnabled()) logger.debug("jndiUrl connection made " + jndiUrl );
	}
	
	/**
	 * Returns a new session instance.
	 * @return AMQP Session instance.
	 */
	public Session getSession() {
		Session session = null;
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (Exception e) {
            //logger.error("Unable to create an AMQP session. Make sure the Qpid broker is reachable.", e);
		}
        //logger.debug("AMQP session created");
		return session;
	}

}
