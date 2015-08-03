package unico.resteasy;

import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.GenericType;
import org.junit.Assert;
import org.junit.Test;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import unico.resteasy.User;

/**
 * Run this test against a container when application deployed <i>only</i>
 * @author K. Flattery
 */
public class RestResourceTest {

	static final String ROOT_URL = "http://localhost:8080/resteasy-queue/rest/";
	
	@Test
	public void testPush() throws Exception {
		ClientRequest request = new ClientRequest(ROOT_URL + "users/push/1%3B;444");
		//ClientRequest request = new ClientRequest(ROOT_URL + "push/2;1");
		ClientResponse<String> response = request.get(String.class);
		String statusXML = response.getEntity();
		Assert.assertTrue("URL didn't resolve", statusXML.indexOf("is not available")<0);
		Assert.assertNotNull(statusXML);		
	}
	
	
	public void testFullQueueHistory() throws Exception {
		//ClientRequest request = new ClientRequest(ROOT_URL + "users/push/list");		
		com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create();		 
		WebResource webResource = client.resource(ROOT_URL + "users/push/list");
		com.sun.jersey.api.client.ClientResponse response = webResource.accept(MediaType.TEXT_HTML)
                   .get(com.sun.jersey.api.client.ClientResponse.class);
		if (response.getStatus() != 200 && response.getStatus() != 406) {
		   throw new RuntimeException("Failed : HTTP error code : "	+ response.getStatus());
		}
		String output = response.getEntity(String.class);
		Assert.assertTrue("testing JSON format", output.charAt(0) == '[');
		
		/*ClientResponse<List<Integer>> response = request.get() ;
		List<Integer> allEntries = response.getEntity();
		Assert.assertTrue("", allEntries.size() > 0);*/
	}
	
	//TODO: find Apache commons implementation which seds the whole URL at once
	/*private String escape(String s) {
		if (s.equals("=")) {
			return "%3D";
		} else if (s.equals("?")) {
			return;
		} else if (s.equals("&")) {
			return;
		}
	}*/
}