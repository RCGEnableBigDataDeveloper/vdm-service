package com.rcggs.vdm.service.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VdmServerContext {

	final private static Properties prop = new Properties();

	public static String getProperty(final String name) {
		return prop.getProperty(name);
	}

	static {
		
		InputStream input = null;
		try {
			input = VdmServerContext.class
					.getResourceAsStream("/vdm-service.properties");
			prop.load(input);

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