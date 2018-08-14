package com.rcggs.vdm.service.hdfs;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import com.google.common.base.Throwables;

public class HdfsService {

	public static void put(final String path) {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(path));
			Configuration conf = new Configuration();
			System.out.println("Connecting to -- " + conf.get("fs.defaultFS"));
			FileSystem fs = FileSystem.get(URI.create(path), conf);
			OutputStream out = fs.create(new Path(path));
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
