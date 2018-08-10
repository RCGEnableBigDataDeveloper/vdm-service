package com.rcggs.vdm.service;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcggs.vdm.service.model.RawFile;
import com.rcggs.vdm.service.model.RawFile_;

public class VdmControllerTest {

	public static void main(String[] args) {

		try {

			String json = IOUtils.toString(VdmControllerTest.class.getResourceAsStream("/rawfile.json"));

			// ObjectMapper mapper = new ObjectMapper();
			//
			// RawFile map = mapper.readValue(json, RawFile.class);
			//
			// System.out.println(map.getRawFile().getName());
			//
		
		
		

		RawFile rawFile = new RawFile();
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target("http://localhost:9988/");
		WebTarget employeeWebTarget = webTarget.path("vdm/rawfile");
		Invocation.Builder invocationBuilder = employeeWebTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.entity(json, MediaType.APPLICATION_JSON));
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

//		RawFile rawFile = new RawFile();
//		Client client = ClientBuilder.newClient();
//		WebTarget webTarget = client.target("http://localhost:9988/");
//		WebTarget employeeWebTarget = webTarget.path("vdm/run");
//		Invocation.Builder invocationBuilder = employeeWebTarget.request(MediaType.APPLICATION_JSON);
//		Response response = invocationBuilder.post(Entity.entity(rawFile, MediaType.APPLICATION_JSON));
	}
}
