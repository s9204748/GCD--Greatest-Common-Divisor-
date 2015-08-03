package unico.resteasy;

import java.sql.SQLException;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface SoapResource {

	@WebMethod(operationName = "gcd")
	@WebResult(name = "theGCD")
	public abstract Integer gcd();

	@WebMethod(operationName = "gcdList")
	public List<Integer> gcdList()  throws SQLException;
	
	@WebMethod(operationName = "gcdSum")
	public int gcdSum()  throws SQLException ;
}