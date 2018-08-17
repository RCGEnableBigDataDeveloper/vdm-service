package com.rcggs.vdm.service.trifacta;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Test {

	public static void main(String[] args) {

		//http://52.201.45.52:3005/data/101/644
		
		// createDataset();
		//createWrangledDataSet();
		createFlow();
	}

	static void createConnection() {
		String body = "{ \"name\": \"sqlserver\", \"description\": \"\", \"isGlobal\": false, \"type\": \"jdbc\", \"host\": \"sqlserver.example.com\", \"port\": 1433, \"vendor\": \"sqlserver\", \"vendorName\": \"sqlserver\", \"params\": { \"connectStrOpts\": \"\" }, \"ssl\": false, \"credentialType\": \"basic\", \"credentials\": [ { \"username\": \"sa\", \"password\": \"sa"
				+ "" + "\" } ] }";

	}

	static void createFlow() {
		try {

			String createFlow = "{\"name\": \"My Flow222\",\"description\": \"This is my flow.\"}";

			CloseableHttpClient client = HttpClients.createMinimal();
			HttpPost httpPost = new HttpPost("http://52.201.45.52:3005/v4/flows/");
			httpPost.setHeader("Content-type", "application/json");
			httpPost.setEntity(new StringEntity(createFlow));
			System.err.println(createFlow);

			UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin@trifacta.local", "admin");
			httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

			CloseableHttpResponse response = client.execute(httpPost);

			System.out.println(response.getEntity());

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			System.out.println(response.getStatusLine());
			HttpEntity entity2 = response.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			// EntityUtils.consume(entity2);

			String responseString = EntityUtils.toString(entity2, "UTF-8");

			ObjectMapper mapper = new ObjectMapper();
						// convert JSON string to Map
			JsonNode map = mapper.readTree(responseString);

					System.out.println(map.get("id"));
			


			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void createDataset() {

		try {

			String createDatset = "{ \"path\": \"/data/\", \"type\": \"hdfs\", \"bucket\": null, \"name\": \"311_vendor_demo.json\", \"description\": \"POS-r02 - copy\" }";

			CloseableHttpClient client = HttpClients.createMinimal();
			HttpPost httpPost = new HttpPost("http://52.201.45.52:3005/v4/importedDatasets/");
			httpPost.setHeader("Content-type", "application/json");
			httpPost.setEntity(new StringEntity(createDatset));
			System.err.println(createDatset);

			UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin@trifacta.local", "admin");
			httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

			CloseableHttpResponse response = client.execute(httpPost);

			System.out.println(response.getEntity());

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			System.out.println(response.getStatusLine());
			HttpEntity entity2 = response.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			// EntityUtils.consume(entity2);

			String responseString = EntityUtils.toString(entity2, "UTF-8");
			System.out.println(responseString);

			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void createWrangledDataSet() {
		// Connection conn = null;
		// try {
		// Class.forName("org.apache.hive.jdbc.HiveDriver");
		// conn = DriverManager.getConnection(
		// "jdbc:hive2://192.168.56.10:10000/default");
		//
		// Statement st = conn.createStatement();
		// // st.executeUpdate("CREATE TABLE fastdata.RULE(SEQ BIGINT, NAME
		// // VARCHAR CONSTRAINT rpk PRIMARY KEY (SEQ))");
		// ResultSet rs = st.executeQuery("select rul_evt_data from rul_evt
		// order by rul_evt_tstmp desc limit 100");
		// while (rs.next()) {
		// System.err.println(rs.getString(1));
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		try {

			String createWrangledDtaaset = "{ \"name\": \"Copy of Imported Dataset 2\", \"importedDataset\": { \"id\": 231 }, \"flow\": { \"id\": 104 } }";

			CloseableHttpClient client = HttpClients.createMinimal();
			HttpPost httpPost = new HttpPost("http://52.201.45.52:3005/v4/wrangledDatasets/");
			httpPost.setHeader("Content-type", "application/json");
			httpPost.setEntity(new StringEntity(createWrangledDtaaset));
			System.err.println(createWrangledDtaaset);

			UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin@trifacta.local", "admin");
			httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

			CloseableHttpResponse response = client.execute(httpPost);

			System.out.println(response.getEntity());

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			System.out.println(response.getStatusLine());
			HttpEntity entity2 = response.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			// EntityUtils.consume(entity2);

			String responseString = EntityUtils.toString(entity2, "UTF-8");
			System.out.println(responseString);

			client.close();

			// CloseableHttpClient client = HttpClients.createMinimal();
			// HttpGet httpPost = new
			// HttpGet("http://ec2-23-23-22-145.compute-1.amazonaws.com:3005/v3/connections/");
			// httpPost.setHeader("Content-type", "application/json");
			//
			// UsernamePasswordCredentials creds = new
			// UsernamePasswordCredentials("admin@trifacta.local", "admin");
			// httpPost.addHeader(new BasicScheme().authenticate(creds,
			// httpPost,
			// null));
			//
			// CloseableHttpResponse response = client.execute(httpPost);
			//
			// String responseString =
			// EntityUtils.toString(response.getEntity(),
			// "UTF-8");
			// System.out.println(responseString);
			// client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
