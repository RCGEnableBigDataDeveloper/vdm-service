package com.rcggs.vdm.service.resource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.rcggs.vdm.service.hdfs.HdfsService;
import com.rcggs.vdm.service.model.RawFile;

@Path("/vdm")
public class VdmController {

	final Logger logger = LoggerFactory.getLogger(getClass());

	ComboPooledDataSource cpds;

	public VdmController() {
		cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass("org.postgresql.Driver");
			cpds.setJdbcUrl("jdbc:postgresql://localhost/vdm");
			logger.info("connected to postgres sql");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GET
	@Path("getConnections")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getConnections() {

		String result = null;
		try {
			result = IOUtils
					.toString(getClass()
							.getResourceAsStream("/connections.txt"), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Response r = Response.status(201).entity(result).build();

		r.getHeaders().add("Access-Control-Allow-Origin", "*");
		r.getHeaders().add("Access-Control-Allow-Methods",
				"GET, POST, DELETE, PUT");
		return r;
	}

	@GET
	@Path("rawfile/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getRawFile(@PathParam("id") String id) {

		System.out.println("calling getRawFile " + id);

		String result = null;
		String path = null;

		try {
			Connection conn = cpds.getConnection();
			Statement st = conn.createStatement();
			String qry = String
					.format("SELECT ITEM_PATH, ITEM_NM FROM vdm.SERVICE_CATALOG WHERE ITEM_ID='%s'",
							id);
			System.out.println(qry);
			ResultSet rs = st.executeQuery(qry);
			while (rs.next()) {
				path = rs.getString(1) + rs.getString(2);
			}

			result = HdfsService.get(path);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.status(201).entity(result).build();
	}

	@GET
	@Path("rawfile")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getRawFileList() {

		System.out.println("calling getRawFileList");
		HdfsService.get("/tmp/Customers.csv");
		String result = UUID.randomUUID().toString();
		return Response.status(201).entity(result).build();
	}

	@POST
	@Path("rawfile")
	@Produces(MediaType.TEXT_PLAIN)
	public Response putRawfile(final String json) {

		String result = null;
		try {

			System.err.println(json);
			ObjectMapper mapper = new ObjectMapper();
			RawFile map = mapper.readValue(json, RawFile.class);
			System.out.println(map.getRawFile().getName());

			result = UUID.randomUUID().toString();
			Connection conn = cpds.getConnection();
			Statement st = conn.createStatement();
			String qry = String
					.format("INSERT INTO vdm.SERVICE_CATALOG(ITEM_ID, ITEM_NM, ITEM_PATH)VALUES('%s','%s','%s')",
							result, map.getRawFile().getName(), map
									.getRawFile().getLocation());
			System.out.println(qry);
			st.executeUpdate(qry);

			HdfsService.put(map.getRawFile().getLocation()
					+ map.getRawFile().getName());

		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = Response.status(201).entity(result).build();

		r.getHeaders().add("Access-Control-Allow-Origin", "*");
		r.getHeaders().add("Access-Control-Allow-Methods",
				"GET, POST, DELETE, PUT");
		
		return r;
	}
}