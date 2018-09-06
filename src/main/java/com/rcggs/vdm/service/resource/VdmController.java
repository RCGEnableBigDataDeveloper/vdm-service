package com.rcggs.vdm.service.resource;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.rcggs.vdm.service.context.VdmServerContext;
import com.rcggs.vdm.service.hdfs.HdfsService;
import com.rcggs.vdm.service.model.ConnectionConfig;
import com.rcggs.vdm.service.model.Level;
import com.rcggs.vdm.service.model.RawFile;
import com.rcggs.vdm.service.trifacta.TrifactaClient;
import com.rcggs.vdm.service.util.HttpUtil;

@Path("/vdm")
public class VdmController {

	final Logger logger = LoggerFactory.getLogger(getClass());

	ComboPooledDataSource cpds;

	private TrifactaClient trifactaClient = new TrifactaClient();

	private ObjectMapper mapper = new ObjectMapper();

	public static void main(String[] args) {

	}

	public VdmController() {
		cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass(VdmServerContext.getProperty("db.driver"));
			cpds.setJdbcUrl(VdmServerContext.getProperty("db.url"));
			cpds.setUser(VdmServerContext.getProperty("db.user"));
			cpds.setPassword(VdmServerContext.getProperty("db.password"));
			logger.info("connected to postgres sql");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GET
	@Path("getConformedDataElements")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getConformedDataElements() {

		String result = null;
		Response r = null;
		List<Level> top = new ArrayList<>();
		Level parentLevel = new Level();
		parentLevel.setName("Available Data Objects");
		parentLevel.setId("0");
		parentLevel.setText("Available Data Objects");

		Level add = new Level();
		add.setName("public");
		top.add(add);

		parentLevel.setChildren(top);
		try {
			result = mapper.writeValueAsString(parentLevel);
		} catch (Exception e) {
			e.printStackTrace();
		}

		r = Response.status(201).entity("[" + result + "]").build();

		r.getHeaders().add("Access-Control-Allow-Origin", "*");
		r.getHeaders().add("Access-Control-Allow-Methods",
				"GET, POST, DELETE, PUT");

		return r;

	}

	@GET
	@Path("getDataElements")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDataElements() {

		String result = null;
		Response r = null;

		List<Level> top = new ArrayList<>();

		try {

			String wrangled = HttpUtil
					.get("http://52.201.45.52:3005/v4/wrangledDatasets");
			JsonNode wrangledNode = mapper.readTree(wrangled);
			Iterator<JsonNode> w = wrangledNode.get("data").elements();

			while (w.hasNext()) {
				JsonNode wrangledDetail = w.next();

				String id = wrangledDetail.get("id").asText();
				String data = HttpUtil
						.get("http://52.201.45.52:3005/v4/flowNodes/" + id
								+ "/scriptGraph");
				JsonNode wrangledNodeData = mapper.readTree(data);

				ArrayNode orders = (ArrayNode) wrangledNodeData.get("nodeIds");
				int latest = 0;
				if (orders != null) {
					latest = orders.get(0).asInt();
				}

				String location = wrangledNodeData.get("nodeToLines")
						.get(String.valueOf(latest)).get(0).get("task")
						.get("parameters").get("location").get("value")
						.asText();

				boolean isLocation = true;

				JsonNode locationNode = null;
				try {
					locationNode = mapper.readTree(location);
				} catch (Exception e) {
					isLocation = false;
				}

				Level parentLevel = new Level();
				parentLevel.setName("Available Data Elements");
				parentLevel.setId("0");
				parentLevel.setText("Available Data Elements");

				Level dataSet = new Level();
				ConnectionConfig config = new ConnectionConfig();

				if (!isLocation) {
					dataSet.setName(location.substring(location
							.lastIndexOf("/") + 1));
					dataSet.setText(location.substring(location
							.lastIndexOf("/") + 1));
					config.setHost("host");
					dataSet.setParent("parent");

					Map<String, Object> dataMap = new HashMap<>();
					dataMap.put("config", config);
					dataSet.setData(dataMap);

				} else {
					dataSet.setName(locationNode.get("jdbcTable").asText());
					dataSet.setText(locationNode.get("jdbcTable").asText());
					config.setHost("host");
					dataSet.setParent("parent");

					Map<String, Object> dataMap = new HashMap<>();
					dataMap.put("config", config);
					dataSet.setData(dataMap);

				}

				String s = HttpUtil
						.get("http://52.201.45.52:3005/v4/flowNodes/" + id
								+ "/scriptGraph");
				JsonNode n = mapper.readTree(s);

				if (!isLocation) {
					Iterator<JsonNode> i = n.get("nodeToLines").elements();

					while (i.hasNext()) {
						JsonNode x = i.next();

						if (x != null) {

							Iterator<JsonNode> js = x.elements();

							while (js.hasNext()) {

								List<Level> children = new LinkedList<>();
								JsonNode task = js.next().get("task");
								String taskType = task.get("type").asText();
								JsonNode annotations = task.get("annotations")
										.get("outputColumnAnnotations");

								if (annotations != null) {

									if (!taskType.toLowerCase().equals(
											"replacepatterns")) {
										Iterator<Entry<String, JsonNode>> columns = annotations
												.fields();

										while (columns.hasNext()) {
											Entry<String, JsonNode> e = columns
													.next();

											String column = e.getKey();
											Level l = new Level();
											l.setName(column);
											l.setId(UUID.randomUUID()
													.toString());
											l.setText(column);
											config.setHost("host");
											l.setParent("parent");
											l.setItemType("Data Element");
											l.setType("data");

											Map<String, Object> dataMap = new HashMap<>();
											dataMap.put("config", config);
											l.setData(dataMap);

											children.add(l);
										}

										dataSet.setChildren(children);
									} else {

										// System.err.println(taskType + " -- "
										// + annotations + " /n" + x);

									}
								}
							}
						}
					}
				} else {
					List<Level> jdbcLevels = new LinkedList<>();
					Iterator<JsonNode> jdbc = n.get("nodeToLines").elements();
					while (jdbc.hasNext()) {
						JsonNode jdbcAnnotations = jdbc.next().get(0)
								.get("task").get("annotations")
								.get("outputColumnAnnotations");
						if (jdbcAnnotations != null) {
							Iterator<Entry<String, JsonNode>> jdbcAnnotationsIterator = jdbcAnnotations
									.fields();
							while (jdbcAnnotationsIterator.hasNext()) {
								Entry<String, JsonNode> columns = jdbcAnnotationsIterator
										.next();
								Level level = new Level();
								level.setName(columns.getKey());
								jdbcLevels.add(level);
							}
						}
					}

					dataSet.setChildren(jdbcLevels);
				}

				top.add(dataSet);

				parentLevel.setChildren(top);

				result = mapper.writeValueAsString(parentLevel);
				System.err.println("[" + result + "]");

				String confromed = null;
				List<Level> top1 = new ArrayList<>();
				Level parentLevel1 = new Level();
				parentLevel1.setName("Conformed Data Elements");
				parentLevel1.setId("0");
				parentLevel1.setText("Conformed Data Elements");

				Level add = new Level();
				add.setName("unassigned");
				top1.add(add);

				parentLevel1.setChildren(top1);
				
				

				String confromed2= null;
				List<Level> top2 = new ArrayList<>();
				Level parentLevel2 = new Level();
				parentLevel2.setName("Conformed Data Objects");
				parentLevel2.setId("0");
				parentLevel2.setText("Conformed Data Objects");

				Level add2 = new Level();
				add2.setName("unassigned");
				top2.add(add2);

				parentLevel2.setChildren(top2);
				
				
				try {
					confromed = mapper.writeValueAsString(parentLevel1);
					confromed2 = mapper.writeValueAsString(parentLevel2);
				} catch (Exception e) {
					e.printStackTrace();
				}

				r = Response
						.status(201)
						.entity("{\"datasources\" : [" + result
								+ "], \"conformed\" : " + confromed
								+ ", \"object\" : " + confromed2 + "}").build();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		r.getHeaders().add("Access-Control-Allow-Origin", "*");
		r.getHeaders().add("Access-Control-Allow-Methods",
				"GET, POST, DELETE, PUT");

		return r;
	}

	@GET
	@Path("getWrangledSets")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getWrangledSets() {

		String result = HttpUtil
				.get("http://52.201.45.52:3005/v4/wrangledDatasets");

		Response r = Response.status(201).entity(result).build();

		r.getHeaders().add("Access-Control-Allow-Origin", "*");
		r.getHeaders().add("Access-Control-Allow-Methods",
				"GET, POST, DELETE, PUT");
		return r;
	}

	@GET
	@Path("getConnections")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getConnections() {

		String result = null;
		try {
			// result = IOUtils
			// .toString(getClass()
			// .getResourceAsStream("/connections.txt"), "UTF-8");

		} catch (Exception e) {
			e.printStackTrace();
		}

		Response r = null;

		try {

			Level l = HdfsService.getConnections();

			result = mapper.writeValueAsString(l);

			System.out.println(result);

			r = Response.status(201).entity(result).build();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

		Map<String, Object> data = new HashMap<>();
		String result = null;
		Response r = null;

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

			String path = map.getRawFile().getLocation();
			String name = map.getRawFile().getName();

			if (VdmServerContext.getProperty("kerberos.principal") != null) {
				putHdfs(name, path);

			} else {
				HdfsService.put(name, path);
			}

			// name = name.replaceAll("//*", "_");
			System.out.println(VdmServerContext.getProperty("hdfs.path") + "/"
					+ name);
			name = VdmServerContext.getProperty("hdfs.path") + name;
			int flowId = trifactaClient.createFlow(name + "_flow", name
					+ "_flow description");
			int dataSetId = trifactaClient.importDataset(name, "hdfs",
					"dataset " + name, "dataset " + name + "description");
			int wrangledId = trifactaClient.createWrangledDataset(name
					+ "_wrangled", dataSetId, flowId);

			data.put("flowId", flowId);
			data.put("wrangledId", wrangledId);
			data.put("dataSetId", dataSetId);
			data.put("url", String.format(
					"http://52.201.45.52:3005/data/%d/%d", flowId, wrangledId));
			data.put("name", name);

			r = Response.status(201).entity(mapper.writeValueAsString(data))
					.build();

		} catch (Exception e) {
			e.printStackTrace();
		}

		r.getHeaders().add("Access-Control-Allow-Origin", "*");
		r.getHeaders().add("Access-Control-Allow-Methods",
				"GET, POST, DELETE, PUT");

		return r;
	}

	void putHdfs(final String name, final String path) {

		UserGroupInformation ugi = null;
		try {
			ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(
					VdmServerContext.getProperty("kerberos.principal"),
					VdmServerContext.getProperty("kerberos.keytab"));
			ugi.doAs(new PrivilegedExceptionAction<Void>() {
				public Void run() throws Exception {
					HdfsService.put(name, path);
					return null;
				}
			});
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}