package unico.resteasy;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.*;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * JMS Service
 * @author K. Flattery
 */
@Startup
@Singleton
public class QueueService {

	private final static Logger LOGGER = Logger.getLogger(QueueService.class.getName());
	private String destinationName = "java:/queue/test";
	//private MessageProducer producer;
	private Session session;
	private Queue queue;
	//private MBeanServerConnection server;
	private ConnectionFactory cf;
	private Connection connection;
	
	public QueueService() { // throws NamingException, JMSException {
		try {
			createProducer();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private MessageProducer createProducer() throws NamingException, JMSException {
		Connection connection = null;
		try {
			return createSession().createProducer(queue);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	private Session createSession() throws NamingException, JMSException {
		connection = getConnection();
		queue = (Queue) new InitialContext().lookup(destinationName);
		return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}
	
	private Connection getConnection() throws NamingException, JMSException {
		if (connection == null) {
			Context ic = new InitialContext();
			cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
			connection = cf.createConnection();
		}
		return connection;
	}
	
	public void push(String value) throws NamingException, JMSException {
		Connection connection = getConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		TextMessage message = session.createTextMessage(value);
		// publish the message to the defined Queue
		createProducer().send(message);
		connection.close();
	}

	/**
	 * @return value at top of queue
	 * @throws NamingException 
	 * @throws JMSException 
	 */
	public int pop() throws JMSException, NamingException {
		MessageConsumer consumer = createSession().createConsumer(queue);
		Message msg = consumer.receive(1);
		if (msg != null) {
			if (msg instanceof TextMessage) {
				TextMessage message = (TextMessage) msg;
				LOGGER.log(Level.INFO, "Reading message: " + message.getText());
				return Integer.parseInt(message.getText());
			} else {
				throw new UnsupportedOperationException("cannot process message: " +
						 msg.getClass().getName());
			}
		}
		return -1;
	}
}
