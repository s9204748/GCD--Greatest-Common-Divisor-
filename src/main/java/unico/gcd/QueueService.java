package unico.gcd;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * JMS Service
 * @author K. Flattery
 */
public class QueueService {

	private final static Logger LOGGER = Logger.getLogger(QueueService.class.getName());
	public final static int NO_MESSAGES = -7;
	private static final long TIMEOUT = 10000L;
	private String destinationName = "java:/queue/test";
	private Session session;
	private Queue queue;
	private ConnectionFactory cf;
	private Connection connection;
	private MessageProducer producer;
	
	public QueueService() { // throws NamingException, JMSException {
		try {
			producer = getProducer();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private MessageProducer getProducer() throws NamingException, JMSException {
		if (producer == null) {
				producer = createSession().createProducer(queue);			
		}
		return producer;
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
		connection = getConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		TextMessage message = session.createTextMessage(value);
		// publish the message to the defined Queue
		getProducer().send(message);
		closeConnection();
	}

	/**
	 * @return value at top of queue
	 * @throws NamingException 
	 * @throws JMSException 
	 */
	public int pop() throws JMSException, NamingException {
		try {			
			MessageConsumer consumer = createSession().createConsumer(queue);
			connection.start();
			Message msg = consumer.receive(TIMEOUT);
			if (msg != null) {
				if (msg instanceof TextMessage) {
					TextMessage message = (TextMessage) msg;
					LOGGER.log(Level.INFO, "Reading message: " + message.getText());
					return Integer.parseInt(message.getText());
				} else {
					throw new UnsupportedOperationException("cannot process message: " + msg.getClass().getName());
				}
			}
			return NO_MESSAGES;
		} finally {
			closeConnection();
		}
	}
	
	private void closeConnection() throws JMSException {
		if (producer != null) {
			producer.close();
			producer = null;
		}			
		if (connection != null) {
			connection.close();
			connection = null;
		}		
	}
}
