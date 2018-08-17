package com.rcggs.vdm.test.resource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

public class VdmControllerTest {

	public static void main(String[] args) {

//		try {
//
//			String json = IOUtils.toString(VdmControllerTest.class
//					.getResourceAsStream("/rawfile.json"), "UTF-8");
//
//			Client client = ClientBuilder.newClient();
//			WebTarget webTarget = client.target("http://localhost:9988/");
//			WebTarget employeeWebTarget = webTarget.path("vdm/rawfile");
//			Invocation.Builder invocationBuilder = employeeWebTarget
//					.request(MediaType.APPLICATION_JSON);
//			Response response = invocationBuilder.post(Entity.entity(json,
//					MediaType.APPLICATION_JSON));
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		try {

			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target("http://localhost:9988/");
			WebTarget employeeWebTarget = webTarget.path("vdm/test");
			Invocation.Builder invocationBuilder = employeeWebTarget
					.request(MediaType.APPLICATION_JSON);
			Response response = invocationBuilder.get();
			System.out.println("done");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// RawFile rawFile = new RawFile();
		// Client client = ClientBuilder.newClient();
		// WebTarget webTarget = client.target("http://localhost:9988/");
		// WebTarget employeeWebTarget = webTarget.path("vdm/run");
		// Invocation.Builder invocationBuilder =
		// employeeWebTarget.request(MediaType.APPLICATION_JSON);
		// Response response = invocationBuilder.post(Entity.entity(rawFile,
		// MediaType.APPLICATION_JSON));
	}
}
