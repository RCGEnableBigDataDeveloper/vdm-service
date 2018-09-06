package com.rcggs.vdm.service.hdfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.Logger;

import com.rcggs.vdm.service.model.ConnectionConfig;
import com.rcggs.vdm.service.model.ItemDefinition;
import com.rcggs.vdm.service.model.Level;

public class HdfsConnection extends AbstractDataLakeConnection implements
		DataLakeConnection {

	private final Logger logger = Logger.getLogger(getClass());

	private FileSystem fileSystem;
	private ConnectionConfig config;
	private Configuration conf = null;
	List<Level> top = new ArrayList<>();

	public HdfsConnection(ConnectionConfig config) {
		super(config);
		this.config = config;

		try {

			conf = new Configuration();
			conf.set("fs.hdfs.impl",
					"org.apache.hadoop.hdfs.DistributedFileSystem");

			if (config.getProperties() != null
					&& Boolean.parseBoolean(config.getProperties().getProperty(
							"use.ha.mode"))) {

				String nameservices = (String) config.getProperties().get(
						"nameservices");
				logger.info("hdfs connection using HA mode with nameservice :  "
						+ nameservices);
				String namenode1 = ((String) config.getProperties().get(
						"namenode1"));
				String namenode2 = ((String) config.getProperties().get(
						"namenode2"));

				conf.set(FileSystem.FS_DEFAULT_NAME_KEY, "hdfs://"
						+ nameservices);
				conf.set("dfs.nameservices", nameservices);
				conf.set("dfs.ha.namenodes." + nameservices, namenode1 + ","
						+ namenode2);
				conf.set(
						"dfs.namenode.rpc-address." + nameservices + "."
								+ namenode1,
						(String) config.getProperties().get(
								"namenode1.rpc.address"));
				conf.set(
						"dfs.namenode.rpc-address." + nameservices + "."
								+ namenode2,
						(String) config.getProperties().get(
								"namenode2.rpc.address"));

				conf.set("dfs.client.failover.proxy.provider." + nameservices,
						"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
			} else {
				// -- remove for mapr
				conf.set(FileSystem.FS_DEFAULT_NAME_KEY,
						"hdfs://" + config.getHost() + ":" + config.getPort()
								+ config.getPath());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, ItemDefinition> start(String baseUri)
			throws IOException {
		FileStatus[] status = fileSystem.listStatus(new Path(baseUri));
		return browse(baseUri, status, "/");
	}

	List<ItemDefinition> dirs = new ArrayList<ItemDefinition>();
	Map<String, ItemDefinition> items = new LinkedHashMap<String, ItemDefinition>();

	private Map<String, ItemDefinition> browse(String baseUri,
			FileStatus[] status, String curDirName)
			throws FileNotFoundException, IOException {

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);
		Map<String, HdfsPathInfo> map = new HashMap<String, HdfsPathInfo>();
		boolean showFiles = true; // Boolean.parseBoolean(config.getProperties().getProperty("show.files"));

		for (int i = 0; i < status.length; i++) {

						
			FileStatus fileStatus = status[i];
			String path = fileStatus.getPath().toString();
			path = path.substring(path.indexOf(baseUri)).substring(
					baseUri.length());

			boolean skip = false;
			for (String exclude : config.getProperties()
					.getProperty("excludes").split(",")) {
				if (path.startsWith(exclude))
					skip = true;
			}

			if (!skip) {

				// ContentSummary cs =
				// fileSystem.getContentSummary(fileStatus.getPath());
				// long fileCount = cs.getFileCount();
				// long dirCount = cs.getFileCount();

				int count = 0;
				boolean recursive = false;
				// RemoteIterator<LocatedFileStatus> ri =
				// fileSystem.listFiles(fileStatus.getPath(), recursive);
				// while (ri.hasNext()) {
				// count++;
				// ri.next();
				// }
				data.put("length", count);

				if (!fileStatus.isDirectory()) {

					
					// if (fileCount == 1) {
					// String str = path.replaceFirst(".*/(\\w+)", "$1");
					// if (str.matches("part-.-\\d{5}")) {
					ItemDefinition idef = new ItemDefinition();
					if (path.lastIndexOf("/") > 0) {
						path = path.substring(0, path.lastIndexOf("/"));					
					}

					idef.setName(path);
					idef.setData(data);
					idef.setItemType(fileStatus.isDirectory() ? "directory"
							: "file");
					idef.setType(config.getType());
					idef.setParent(config.getPath());
					items.put(path, idef);
				}
			}

			if (fileStatus.isDirectory()) {

				
				FileStatus[] subStatus = fileSystem.listStatus(fileStatus
						.getPath());
				browse(baseUri, subStatus, path);

			} else {

				if (showFiles) {

					if (!skip) {
						
						String name =curDirName + "/"
								+ fileStatus.getPath().getName();
					
						ItemDefinition id2 = new ItemDefinition();
						id2.setName(name);
						id2.setType("file");
						id2.setItemType("file");
						items.put(name, id2);
						
						System.err.println(curDirName + "/"
								+ fileStatus.getPath().getName());
					}

					if (map.containsKey(curDirName)) {
						map.get(curDirName).getSize()
								.addAndGet(fileStatus.getLen());
					} else {
						HdfsPathInfo pathInfo = new HdfsPathInfo();
						pathInfo.setSize(new AtomicLong(fileStatus.getLen()));
						pathInfo.setAccessTime(fileStatus.getAccessTime());
						pathInfo.setBlockSize(fileStatus.getBlockSize());
						pathInfo.setLastModifed(fileStatus
								.getModificationTime());
						pathInfo.setGroup(fileStatus.getGroup());
						pathInfo.setOwner(fileStatus.getOwner());
						pathInfo.setReplicationFactor(fileStatus
								.getReplication());

						map.put(fileStatus.getPath().getName(), pathInfo);
					}
				}
			}
		}

		return items;
	}

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(final String name)
			throws Exception {

		final Map<ItemDefinition, List<ItemDefinition>> map = new HashMap<ItemDefinition, List<ItemDefinition>>();
		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);

		if (config.getProperties() != null
				&& config.getProperties().getProperty("kerberos.principal") != null) {

			logger.info("hdfs krb user "
					+ config.getProperties().getProperty("kerberos.principal")
					+ ":"
					+ config.getProperties().getProperty("kerberos.keytab"));

			conf.set("hadoop.security.authentication", "Kerberos");
			conf.set("dfs.namenode.kerberos.principal", "nn/_HOST@JCI.COM");
			UserGroupInformation.setConfiguration(conf);
			UserGroupInformation ugi = null;
			try {
				ugi = UserGroupInformation
						.loginUserFromKeytabAndReturnUGI(config.getProperties()
								.getProperty("kerberos.principal"), config
								.getProperties().getProperty("kerberos.keytab"));
				ugi.doAs(new PrivilegedExceptionAction<Void>() {
					public Void run() throws Exception {
						// conf.set("fs.default.name",
						// conf.get(FileSystem.FS_DEFAULT_NAME_KEY));
						fileSystem = FileSystem.get(conf);
						Map<String, ItemDefinition> list = start(config
								.getPath());
						ItemDefinition idef = new ItemDefinition();
						idef.setName(config.getPath());
						idef.setItemType("directory");
						idef.setType(config.getType());
						idef.setData(data);
						map.put(idef,
								new ArrayList<ItemDefinition>(list.values()));
						return null;
					}
				});
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		} else {

			fileSystem = FileSystem.get(conf);
			Map<String, ItemDefinition> list = start(config.getPath());
			ItemDefinition idef = new ItemDefinition();
			idef.setName(config.getPath());
			idef.setItemType("directory");
			idef.setType(config.getType());
			idef.setData(data);
			map.put(idef, new ArrayList<ItemDefinition>(list.values()));

		}
		return map;
	}
}
