package unico.resteasy;

import java.sql.SQLException;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.naming.NamingException;

@WebService
public interface SoapResource {

	@WebMethod(operationName = "gcd")
	@WebResult(name = "theGCD")
	public Integer gcd();

	@WebMethod(operationName = "gcdList")
	public List<Integer> gcdList()  throws SQLException, NamingException;
	
	@WebMethod(operationName = "gcdSum")
	public int gcdSum()  throws SQLException, NamingException ;
}