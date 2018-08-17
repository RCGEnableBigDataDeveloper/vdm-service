package com.rcggs.vdm.test.trifacta;

import junit.framework.TestCase;

import org.junit.Test;

import com.rcggs.vdm.service.trifacta.TrifactaClient;

public class TrifactaClientTest extends TestCase {

	private TrifactaClient trifactaClient = new TrifactaClient();

	@Test
	public void testCreateFlow() {
		int flowId = trifactaClient.createFlow("asdasd", "asdasd");
		assertNotNull(flowId);
	}

	@Test
	public void testImportDataSet() {
		int dataSetId = trifactaClient.importDataset("/data", "hdfs",
				"imprted dataset 123", "description");
		assertNotNull(dataSetId);

	}

	@Test
	public void testCreateWrangledDataset() {
		int flowId = trifactaClient.createFlow("asdasd", "asdasd");
		int dataSetId = trifactaClient.importDataset("/data", "hdfs",
				"imprted dataset 123", "description");
		int wrangledId = trifactaClient.createWrangledDataset("test",
				dataSetId, flowId);
		System.out.println(String.format("http://52.201.45.52:3005/data/%d/%d",
				flowId, wrangledId));
	}

}
