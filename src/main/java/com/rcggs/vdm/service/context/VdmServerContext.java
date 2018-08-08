package com.rcggs.vdm.service.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VdmServerContext {
	public static void main(String[] args) {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = VdmServerContext.class.getResourceAsStream("/vdm-server.properties");
			prop.load(input);
			System.out.println(prop.getProperty("database"));

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}