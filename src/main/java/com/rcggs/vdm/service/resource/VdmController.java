package com.rcggs.vdm.service.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcggs.vdm.service.VdmControllerTest;
import com.rcggs.vdm.service.model.RawFile;

@Path("/vdm")
public class VdmController {

	final Logger logger = LoggerFactory.getLogger(getClass());

	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public Response test() {
		String result = "server running";
		return Response.status(201).entity(result).build();
	}

	@POST
	@Path("rawfile")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response run(final String route) {
		try {

			String json = IOUtils.toString(VdmControllerTest.class.getResourceAsStream("/rawfile.json"), "UTF-8");

			ObjectMapper mapper = new ObjectMapper();

			RawFile map = mapper.readValue(json, RawFile.class);
			
			System.out.println(map.getRawFile().getName());
		
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		String result = "server running";
		return Response.status(201).entity(result).build();
	}

	@POST
	@Path("run")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response run(final RawFile route) {
		System.out.println(route);
		logger.info(route.toString());
		try {

			RawFile file = new RawFile();

			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity(e.getMessage()).build();
		}
	}
}