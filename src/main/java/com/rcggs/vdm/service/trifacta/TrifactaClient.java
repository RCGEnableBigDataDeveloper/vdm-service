package com.rcggs.vdm.service.trifacta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcggs.vdm.service.util.HttpUtil;
import com.rcggs.vdm.service.util.JsonUtil;

public class TrifactaClient {

	public static void main(String[] args) {
		// createFlow("asdasd","asdasd");
		// importDataset("/data", "hdfs", "imprted dataset 123", "description");
		createWrangledDataset("test", 23, 123);
	}

	final static Logger logger = LoggerFactory.getLogger(TrifactaClient.class);

	public static int createFlow(final String name, final String description) {
		String createFlow = String.format(TrifactaTemplate.get("createFlow"),
				name, description);
		logger.info(createFlow);
		String createFlowEP = "http://52.201.45.52:3005/v4/flows/";
		String createFlowResponse = HttpUtil.post(createFlowEP, createFlow);
		logger.info(createFlowResponse);
		return JsonUtil.parse(createFlowResponse).get("id").asInt();
	}

	public static int importDataset(final String name, final String type,
			final String path, final String description) {
		String importDataset = String.format(
				TrifactaTemplate.get("importDataset"), name, type, path,
				description);
		logger.info(importDataset);
		String importDatasetEP = "http://52.201.45.52:3005/v4/importedDatasets/";
		String importDatasetResponse = HttpUtil.post(importDatasetEP,
				importDataset);
		logger.info(importDatasetResponse);
		return JsonUtil.parse(importDatasetResponse).get("id").asInt();
	}

	public static int createWrangledDataset(final String name,
			final int dataSetId, final int flowId) {
		String createWrangledDataset = String.format(
				TrifactaTemplate.get("createWrangledDataset"), name, dataSetId,
				flowId);
		logger.info(createWrangledDataset);
		String createWrangledDatasetEP = "http://52.201.45.52:3005/v4/wrangledDatasets/";
		String createWrangledDatasetResponse = HttpUtil.post(
				createWrangledDatasetEP, createWrangledDataset);
		logger.info(createWrangledDatasetResponse);
		return JsonUtil.parse(createWrangledDatasetResponse).get("id").asInt();
	}
}
