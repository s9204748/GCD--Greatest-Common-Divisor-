package unico.resteasy;

import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Implementation for RestFUL interface
 * @author Asus i7
 */
@Path("/")
@Produces(MediaType.APPLICATION_XML)
public class RestResource {
	
	private static String DELIMETER = ",";//;";
	private static final String ESCAPED_DELIMTER = "%2C;";//%3B;";
	private final static Logger LOGGER = Logger.getLogger(RestResource.class.getName());
	private QueueService queueService = new QueueService();
	private DatabaseService databaseService;

	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getQueueJSON() {
		try {
			List<Integer> list = getDatabaseService().getQueueAudit();
			JSONObject obj = new JSONObject();			
			JSONArray array = new JSONArray();
			array.addAll(list);
			obj.put("queueElements", array);			
			String jsonText = obj.toJSONString();//out.toString();
			LOGGER.log(Level.INFO, "jsonText: " + jsonText);
			return Response.ok(jsonText, MediaType.APPLICATION_JSON	).status(200)
					.header("accept", MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			return Response.ok("<status>failure with JMS: " + e.getMessage() + "</status>").build();
		}
	}

	@Path("/push/{newElements}")
	@GET
	public Response push(@PathParam("newElements") String delimitedList) {
		LOGGER.setLevel(Level.INFO);
		// need to handle both cases of access through browser or otherwise (URL encoding)
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
				e.printStackTrace();
				return renderResponse("failure with JMS", e);
			}
			try {
				getDatabaseService().add(value, DatabaseService.TABLE);
			} catch (Exception e) {
				e.printStackTrace();
				return renderResponse("failure with DataSource", e);
			} 
		}		
		LOGGER.log(Level.INFO, "pushed "+tokenCount+" values onto queue\n");
		return Response.ok("<status>success: "+tokenCount +"</status>").build();
	}

	private DatabaseService getDatabaseService() throws NamingException, SQLException {
		if (databaseService == null) {
			databaseService = new DatabaseService();
		}
		return databaseService;
	}
	
	private Response renderResponse(String s, Exception e) {
		StringBuffer sB = new StringBuffer().append("<status>").append(s);
		if (e != null) {
			sB.append(": "+e.getMessage());
		}
		sB.append("</status>");
		return Response.ok(sB.toString()).build();
	}
	
	@Path("/drop")
	@GET
	public Response dropTables() throws SQLException, NamingException, JMSException {
		getDatabaseService().dropTable(DatabaseService.GCD_TABLE);
		getDatabaseService().dropTable(DatabaseService.TABLE);
		queueService.deleteAllMessagesFromQueue();
		return renderResponse("table dropped", null);
	}
}