package com.rcggs.vdm.service.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/service")
public class EngineController {

	final Logger logger = LoggerFactory.getLogger(getClass());

	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public Response test() {
		String result = "server running";
		return Response.status(201).entity(result).build();
	}

	@POST
	@Path("run")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response run(final String route) {
		System.out.println(route);
		logger.info(route);
		try {

			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity(e.getMessage()).build();
		}
	}
}