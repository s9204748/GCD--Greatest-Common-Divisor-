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
		org.jboss.resteasy.client.ClientRequest request = new org.jboss.resteasy.client.ClientRequest(ROOT_URL
				+ "push/1%3B;444");
		// ClientRequest request = new ClientRequest(ROOT_URL + "push/2;1");
		org.jboss.resteasy.client.ClientResponse<String> response = request.get(String.class);
		String statusXML = response.getEntity();
		Assert.assertTrue("URL didn't resolve", statusXML.indexOf("is not available") < 0);
		Assert.assertNotNull(statusXML);
		// Assert.assertTrue("Checking for success",
		// statusXML.indexOf("<status>success") >= 0);
	}

	/*
	 * public void testFullQueueHistory() throws Exception { ResteasyClient
	 * client = new ResteasyClientBuilder().build(); ResteasyWebTarget target =
	 * client.target(ROOT_URL + "push/list"); Response response =
	 * target.request(MediaType.APPLICATION_JSON).get(); String value =
	 * response.readEntity(String.class); assertJSON(value); }
	 */

	public void testFullQueueHistory() throws Exception {
		// ClientRequest request = new ClientRequest(ROOT_URL + "push/list");
		com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create();
		WebResource webResource = client.resource(ROOT_URL + "list");

		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("json", "js"); // set parametes for request
		// .queryParams(queryParams) .header("Authorization", appKey)
		String appKey = "Bearer " + 7; // appKey is unique number

		webResource.setProperty("Content-Type", "application/json;charset=UTF-8");

		com.sun.jersey.api.client.ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON)
				.header("Content-Type", "application/json;charset=UTF-8")
				.get(com.sun.jersey.api.client.ClientResponse.class);
		if (response.getStatus() != 200 && response.getStatus() != 406) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
		JSONObject jsonOutput = (JSONObject) JSONSerializer.toJSON(response.getEntity(String.class));
		String output = "";
		assertJSON(output);
	}

	@Test
	public void testname() throws Exception {
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

	public void testAllQueueHistory() throws Exception {
		Client client = Client.create();
		WebResource webResource2 = client.resource(ROOT_URL + "push/list");
		// ?json=%7B'selection':%7B'includeAlerts':'true','selectionType':'registered','selectionMatch':'','isTheEvent':'true','includeRuntime':'true'%7D%7D");
		ClientResponse response2 = webResource2.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		if (response2.getStatus() != 200 && response2.getStatus() != 406) {
			throw new RuntimeException("Failed : HTTP error code : " + response2.getStatus());
		}
		String output2 = response2.getEntity(String.class);
		assertJSON(output2);
	}

	private void assertJSON(String output2) {
		Assert.assertTrue("testing start JSON format", output2.charAt(0) == '{');
		String regex = "\\{:[a-zA-Z0-9]+\":\\[[a-zA-Z0-9]*";
		Pattern p = Pattern.compile(regex);
		Assert.assertTrue("testing full JSON format", p.matcher(output2).matches());
	}

	/*
	 * @Test public void testMyResource() { ClientConfig config = new
	 * DefaultClientConfig();
	 * config.getClasses().add(JacksonJaxbJsonProvider.class);
	 * config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
	 * Boolean.TRUE); Client c = Client.create(config); WebResource resource =
	 * c.resource(ROOT_URL); ClientResponse response =
	 * resource.path("/users/push/list")
	 * .accept("application/json").get(ClientResponse.class); String s =
	 * response.getEntity(String.class); assertJSON(s); }
	 */
	// TODO: find Apache commons implementation which seds the whole URL at once
	/*
	 * private String escape(String s) { if (s.equals("=")) { return "%3D"; }
	 * else if (s.equals("?")) { return; } else if (s.equals("&")) { return; } }
	 */
}