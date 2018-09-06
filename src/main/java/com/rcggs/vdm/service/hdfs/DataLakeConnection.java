package com.rcggs.vdm.service.hdfs;

import java.util.List;
import java.util.Map;

import com.rcggs.vdm.service.model.ItemDefinition;

public interface DataLakeConnection {

	public abstract Map<ItemDefinition, List<ItemDefinition>> describe(final String name)
			throws Exception;

}