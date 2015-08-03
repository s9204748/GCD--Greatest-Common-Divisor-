package unico.resteasy;

import java.io.StringWriter;
import java.util.Iterator;

import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONValue;

import unico.resteasy.MockUserTable;

@Path("/users")
@Produces(MediaType.APPLICATION_XML)
public class RestResource {
	
	private static String DELIMETER = ";";
	private static final String ESCAPED_DELIMTER = "%3B;";
	private final static Logger LOGGER = Logger.getLogger(RestResource.class.getName());
	private QueueService queueService = new QueueService();
	private DatabaseService databaseService = new DatabaseService();
	
	@Path("/")
	@GET
	public Response getUsersXML() {
		List<User> users = MockUserTable.getAll();
		GenericEntity<List<User>> ge = new GenericEntity<List<User>>(users) {		};
		return Response.ok(ge).build();
	}

	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getQueueJSON() {
		try {
			List<Integer> list = databaseService.getQueueAudit();
			StringWriter out = new StringWriter();
			JSONValue.writeJSONString(list, out);
			String jsonText = out.toString();
			LOGGER.log(Level.INFO, "jsonText: " + jsonText);
			return Response.ok(jsonText).build();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			return Response.ok("<status>failure with JMS: " + e.getMessage() + "</status>").build();
		}
	}
	
	@Path("/{id}")
	@GET
	public Response getUserXMLById(@PathParam("id") Integer id) {
		return Response.ok(MockUserTable.getById(id)).build();
	}

	@Path("/")
	@POST
	public Response saveUser(User user) {
		MockUserTable.save(user);
		return Response.ok("<status>success</status>").build();
	}

	@Path("/push/{newElements}")
	@GET
	public Response push(@PathParam("newElements") String delimitedList) {
		LOGGER.setLevel(Level.INFO);
		if (delimitedList.indexOf(ESCAPED_DELIMTER) >= 0) {
			DELIMETER = ESCAPED_DELIMTER;
		}
		StringTokenizer sT = new StringTokenizer(delimitedList, DELIMETER);
		int tokenCount = sT.countTokens();
		while (sT.hasMoreTokens()) {
			String value = (String) sT.nextToken();
			LOGGER.log(Level.INFO, "push("+value + ")");
			try {
				queueService.push(value);				
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
				return Response.ok("<status>failure with JMS: "+e.getMessage() +"</status>").build();
			}
			databaseService.add(value, DatabaseService.TABLE);
		}		
		LOGGER.log(Level.INFO, "pushed "+tokenCount+" values onto queue\n");
		return Response.ok("<status>success: "+tokenCount +"</status>").build();
	}
	
	@Path("/{id}")
	@DELETE
	public Response deleteUser(@PathParam("id") Integer id) {
		MockUserTable.delete(id);
		return Response.ok("<status>success</status>").build();
	}
}