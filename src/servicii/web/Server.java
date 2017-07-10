package servicii.web;

import java.util.List;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

// adnotarea Path specifica calea relativa spre un anumit serviciu web
// e.g., @ApplicationPath("rest") + @Path("/hello") => http://localhost:8080/ProiectServiciiWeb/rest/hello
// in aceasta situatie se va apela una din metodele clasei Hello, in functie de tipul media (text/plain, text/xml sau text/html)
@Path("/api")
public class Server {
	@Path("/checkUser/{username}/{password}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public int checkLogin(@PathParam("username") String username, @PathParam("password") String password) {
		return DBManager.getInstance().testLogin(username, password);
	}
	@Path("/tasks")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getTask(){
		String tasks=DBManager.getInstance().task().toString();
		return tasks;
	}

}