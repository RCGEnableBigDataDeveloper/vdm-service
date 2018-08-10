package com.rcggs.vdm.service.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcggs.vdm.service.resource.VdmController;

public class VdmServer {

	final static Logger logger = LoggerFactory.getLogger(VdmServer.class);

	public static void main(String[] args) {

		logger.info("Starting as a Rest server");

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		Server jettyServer = new Server(9988);
		jettyServer.setHandler(context);
		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
				VdmController.class.getCanonicalName());

		try {
			jettyServer.start();
			jettyServer.join();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jettyServer.destroy();

		}
	}
}
