package unico.resteasy;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration tests for {@link unico.resteasy.SoapResource} implementation.
 * @author Kevin Flattery
 */
public class SoapResourceTest {

	private SoapResource client;
	
	private final static String URL = "http://localhost:8080/resteasy-queue/SoapResource";

	@Before
	public void setUp() throws Exception {
		client = createClient();
	}

	@Test
	public void testGCD() throws Exception {
		//prime the queue in case empty
		RestResourceTest.pushValues(20,16);
		int i = client.gcd();
		assertFalse("No messages in Queue to work out GCD", i == -7);
		assertTrue("GCD should be postive value ("+i+")", i >= 0);
	}
	
	@Test
	public void testGcdList() throws Exception {
		List<Integer> gcdList = client.gcdList();
		int size = gcdList.size();
		assertTrue("GCD should be postive value (" + size + ")", size >= 0);
	}
	
	@Test
	public void testGcdSum() throws Exception {
		int gcdSum = client.gcdSum();
		assertTrue("gcd Sum should be postive value ("+gcdSum+")", gcdSum >= 0);
	}
	
	/**
	 *  largest number that divides into both 20 and 16 is 4
	 * @throws Exception
	 */
	@Test
	public void testFindGCD() throws Exception {
		SoapResourceImpl resource = new SoapResourceImpl();
		int findGCD = resource.findGCD(16, 20);
		assertTrue("GCD(16,20) should be 4 but was " + findGCD, findGCD == 4);
	}
	
	static SoapResource createClient() {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.getInInterceptors().add(new LoggingInInterceptor());
		factory.getOutInterceptors().add(new LoggingOutInterceptor());
		factory.setServiceClass(SoapResource.class);
		factory.setAddress(URL);
		SoapResource client = (SoapResource) factory.create();
		return client;
	}
}
