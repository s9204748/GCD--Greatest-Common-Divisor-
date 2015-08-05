package unico.resteasy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import net.sf.json.JSONSerializer;

import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Run this test against a container when application deployed <i>only</i>. Many
 * library conflict issues had to be overcome with validating the JSON response.
 * Hence many commented out methods below.
 * 
 * @author K. Flattery
 */
public class RestResourceTest {

	static final String ROOT_URL = "http://localhost:8080/resteasy-queue/rest/";

	@Test
	public void testPush() throws Exception {
		org.jboss.resteasy.client.ClientResponse<String> response = pushValues(20,16);
		String statusXML = response.getEntity();
		Assert.assertTrue("URL didn't resolve", statusXML.indexOf("is not available") < 0);
		Assert.assertNotNull(statusXML);
		// Assert.assertTrue("Checking for success",
		// statusXML.indexOf("<status>success") >= 0);
		SoapResource soapResource = SoapResourceTest.createClient();
		Assert.assertTrue("gcd(16,20)=4", soapResource.gcd() == 4);
	}

	static org.jboss.resteasy.client.ClientResponse<String> pushValues(int i, int j) throws Exception {
		org.jboss.resteasy.client.ClientRequest request = 
				new org.jboss.resteasy.client.ClientRequest(ROOT_URL + 
						"push/" + String.valueOf(i) + ","+String.valueOf(j));
		return request.get(String.class);		
	}
	
	@Test
	public void testFullQueueHistory() throws Exception {
		// The request also includes the userip parameter which provides the end
		// user's IP address. Doing so will help distinguish this legitimate
		// server-side traffic from traffic which doesn't come from an end-user.
		URL url = new URL(ROOT_URL + "list?userip=USERS-IP-ADDRESS");
		URLConnection connection = url.openConnection();
		connection.addRequestProperty("Referer", "localhost");
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		String jsonString = builder.toString();
		//JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonString);
		assertJSON(jsonString);
	}

	private void assertJSON(String output2) {
		Assert.assertTrue("testing start JSON format", output2.startsWith("{"));
		Assert.assertTrue("testing start JSON format", output2.endsWith("}"));		
		String regex = "\\{:[a-zA-Z0-9]+\":\\[[a-zA-Z0-9]*";
		Pattern p = Pattern.compile(regex);
		//Assert.assertTrue("testing full JSON format", p.matcher(output2).matches());
	}

	// TODO: find Apache commons implementation which seds the whole URL at once
	/*
	 * private String escape(String s) { if (s.equals("=")) { return "%3D"; }
	 * else if (s.equals("?")) { return; } else if (s.equals("&")) { return; } }
	 */
}