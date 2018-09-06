package com.rcggs.vdm.service.hdfs;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import com.google.common.base.Throwables;
import com.rcggs.vdm.service.context.VdmServerContext;
import com.rcggs.vdm.service.model.ConnectionConfig;
import com.rcggs.vdm.service.model.ItemDefinition;
import com.rcggs.vdm.service.model.Level;

public class HdfsService {

	public static Level getConnections() {

		Level fsParent = new Level();
		fsParent.setName(VdmServerContext.getProperty("fs.host") + " (local)");
		fsParent.setText(VdmServerContext.getProperty("fs.host") + " (local)");
		fsParent.setId(UUID.randomUUID().toString());

		List<Level> fsLevelList = new LinkedList<>();
		// fsLevelList.add(fsParent);

		ConnectionConfig c = new ConnectionConfig();
		c.setHost(VdmServerContext.getProperty("fs.host"));
		c.setPath(VdmServerContext.getProperty("fs.path"));
		Properties p = new Properties();
		p.setProperty("excludes", "oozie/,hdfs");
		LocalFileSystemConnection con = new LocalFileSystemConnection(c);

		try {
			Map<ItemDefinition, List<ItemDefinition>> o = con.describe(null);

			System.out.println("Successfully connected to filesystem");
			for (Map.Entry<ItemDefinition, List<ItemDefinition>> entry : o
					.entrySet()) {
				System.out
						.printf("Files under: %s\n", entry.getKey().getName());

				Level fsChildLevel = new Level();
				String name = entry.getKey().getName();
				if (name.contains(VdmServerContext.getProperty("fs.path"))) {
					name = name.substring(VdmServerContext.getProperty(
							"fs.path").length());
				}
				// name = name.substring(name.lastIndexOf("/"));
				if (name.startsWith("/"))
					name = name.substring(1, name.length());
				if (name.length() == 0)
					continue;
				fsChildLevel.setName(name);
				fsChildLevel.setText(name);
				fsChildLevel.setId(UUID.randomUUID().toString());
				fsChildLevel.setData(entry.getKey().getData());
				fsChildLevel.setItemType("Data Source");
				fsLevelList.add(fsChildLevel);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Level main = new Level();
		main.setName("Available Data Sources");
		main.setId(UUID.randomUUID().toString());
		main.setText("Available Data Sources");

		List<Level> top = new ArrayList<>();
		Level parentLevel = new Level();
		parentLevel.setName(VdmServerContext.getProperty("hdfs.host")
				+ " (hdfs)");
		parentLevel.setId(UUID.randomUUID().toString());
		parentLevel.setText(VdmServerContext.getProperty("hdfs.host")
				+ " (hdfs)");
		parentLevel.setItemType("Data Source");

		c = new ConnectionConfig();
		c.setHost(VdmServerContext.getProperty("hdfs.host"));
		c.setPort(VdmServerContext.getProperty("hdfs.port"));
		c.setPath(VdmServerContext.getProperty("hdfs.path"));
		p = new Properties();
		p.setProperty("excludes", "oozie/,hdfs");

		Map<String, Object> data = new HashMap<>();
		data.put("config", c);
		parentLevel.setData(data);

		c.setProperties(p);
		HdfsConnection hdfs = new HdfsConnection(c);
		Map<ItemDefinition, List<ItemDefinition>> o;
		try {
			o = hdfs.describe(null);
			for (Map.Entry<ItemDefinition, List<ItemDefinition>> entry : o
					.entrySet()) {
				System.out.printf("directories under hdfs: %s\n", entry
						.getKey().getName());
				for (ItemDefinition i : entry.getValue()) {
					if (i.getName().startsWith("/"))
						continue;
					System.out.println(i.getName());
					Level add = new Level();
					add.setName(i.getName());
					add.setData(entry.getKey().getData());
					add.setType("hdfs");
					add.setItemType("Data Source");
					add.setId(UUID.randomUUID().toString());
					top.add(add);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Level> parents = new LinkedList<>();
		parents.add(parentLevel);

		fsParent.setChildren(fsLevelList);

		parents.add(fsParent);
		parentLevel.setChildren(top);

		main.setChildren(parents);

		return main;
	}

	public static void put(final String name, final String path) {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(path
					+ "/" + name));
			Configuration conf = new Configuration();
			System.out.println("Connecting to -- " + conf.get("fs.defaultFS"));
			FileSystem fs = FileSystem.get(conf);
			OutputStream out = fs.create(new Path(VdmServerContext
					.getProperty("hdfs.path") + "/" + name));
			IOUtils.copyBytes(in, out, 4096, true);
			System.out.println("done");

		} catch (Exception e) {
			Throwables.getStackTraceAsString(e);
			e.printStackTrace();
		}
	}

	public static String get(final String path) {

		String out = null;

		try {
			Configuration conf = new Configuration();
			System.out.println("Connecting to -- " + conf.get("fs.defaultFS"));
			FileSystem fs = FileSystem.get(URI.create(path), conf);
			Path hdfsreadpath = new Path(path);
			FSDataInputStream inputStream = fs.open(hdfsreadpath);
			out = org.apache.commons.io.IOUtils.toString(inputStream, "UTF-8");
			System.out.println(out);
			inputStream.close();
			fs.close();
		} catch (Exception e) {
			Throwables.getStackTraceAsString(e);
			e.printStackTrace();
		}

		return out;

	}

}
