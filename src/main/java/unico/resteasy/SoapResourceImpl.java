package unico.resteasy;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.naming.NamingException;

import org.springframework.util.Assert;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebService(name = "SoapResource",targetNamespace = "service",endpointInterface = "unico.resteasy.SoapResource")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL,
 parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class SoapResourceImpl implements SoapResource {
	
	private final static Logger LOGGER = Logger.getLogger(SoapResourceImpl.class.getName());
	
	private QueueService queueService;
	private DatabaseService databaseService;
	
	public SoapResourceImpl() {}
	
    /** 
	 * @see unico.resteasy.SoapResource#gcd()
	 */
    @WebMethod(operationName = "gcd")
    @WebResult(name="theGCD")    
    public Integer gcd() {
    	int i = -1, j = -1;
		try {
			queueService = new QueueService();			
			i = queueService.pop();
			j = queueService.pop();
			if (queueService.getQueueSize() >= 2) {
				Assert.isTrue(i >= 0);
				Assert.isTrue(j >= 0);
			}
			int k = findGCD(i, j);
			if (k == QueueService.NO_MESSAGES) {
				LOGGER.log(Level.WARNING, "No messages on queue to calculate GCD!");
			} else {
				LOGGER.log(Level.INFO, "GCD: " + k);
				getDatabaseService().addGCD(k);
			}
			return new Integer(k);			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "" + e.getMessage());
			return new Integer(-2);
		}        		
    }
    
    /**
     * Java method to find GCD of two number using Euclid's method 
     * @return GDC of two numbers in Java 
     */ 
	int findGCD(int number1, int number2) {
		// base case
		if (number2 == 0) {
			return number1;
		}
		return findGCD(number2, number1 % number2);
	}    	   
	
	public List<Integer> gcdList() throws SQLException, NamingException {
		List<Integer> gcdAudit = getDatabaseService().getGCDAudit();
		LOGGER.log(Level.INFO, "GCD audit list: " + gcdAudit);
		return gcdAudit;
	}
	
	public int gcdSum()  throws SQLException, NamingException {
		List<Integer> gcdAudit = getDatabaseService().getGCDAudit();
		int sum = 0;
		for (Iterator iterator = gcdAudit.iterator(); iterator.hasNext();) {
			Integer integer = (Integer) iterator.next();
			sum += integer.intValue();
		}
		return sum;
	}
	
	/**
	 * Allows for member instance to be shared and avoid creation in contructor which
	 * makes unit testing non WS methods difficult outside container.
	 * @return {@link DatabaseService} instance.
	 * @throws SQLException 
	 * @throws NamingException 
	 */
	private DatabaseService getDatabaseService() throws NamingException, SQLException {
		if (this.databaseService == null) {
			this.databaseService = new DatabaseService();
		}
		return this.databaseService;
	}
}
