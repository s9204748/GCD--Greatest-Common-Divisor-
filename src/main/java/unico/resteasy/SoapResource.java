package unico.resteasy;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.resteasy.logging.Logger;

@WebService(name = "SoapResource",targetNamespace = "service")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL,
 parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class SoapResource {
	
	private final static Logger LOGGER = Logger.getLogger(SoapResource.class);
	private QueueService queueService = new QueueService();
	
    @WebMethod(operationName = "gcd")
    @WebResult(name="theGCD")
    public Integer gcd() {
    	int i = -1, j = -1;
		try {
			i = queueService.pop();
			j = queueService.pop();
		} catch (Exception e) {
			LOGGER.error("");
		}
        return findGCD(i, j);
    }
    
    /**
     * Java method to find GCD of two number using Euclid's method 
     * @return GDC of two numbers in Java 
     */ 
	private int findGCD(int number1, int number2) {
		// base case
		if (number2 == 0) {
			return number1;
		}
		return findGCD(number2, number1 % number2);
	}    	   
}
