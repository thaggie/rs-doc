package rs.doc.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import rs.doc.annotations.Doc;
import rs.doc.annotations.ResponseType;

@Path("/foo")
public class ExampleResource {

	@Path("/bar/{id}")
	@GET
	@Doc("This method really raises the bar.")
	@Produces("application/json")
	@ResponseType(Bar.class)
	public Bar bar(
			@Doc("The identifier of the thing that's used to do the thing.") @PathParam("id") String id , 
			@Doc("The name of the baz.") @QueryParam("baz") String baz
			) {
		return new Bar(id, baz);
	}

}