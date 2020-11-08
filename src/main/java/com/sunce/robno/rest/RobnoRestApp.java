package com.sunce.robno.rest;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.DispatcherType;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.RolloverFileOutputStream;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;

import com.sunce.robno.rest.resource.RobnoRestResourceConfig;

public class RobnoRestApp {
	private static final Logger LOG = Log.getLogger(RobnoRestApp.class);

	private static Map<UUID, Integer> loggedUsers = new HashMap<UUID, Integer>();

	private Server server;

	Properties props;

	int serverPort;
	String appName;
	int idleTimeout = 600_000;
	String workResultsIngesterUrl = null;
	String logFile = null;
	int acceptQueueSize = 100;
	int logRetainDays = 30;
	boolean startDispatcher = false;
	int watchdogSleepTime = 60_000;

	public void setup() {

		RobnoRestResourceConfig resourceConfig = new RobnoRestResourceConfig();
		props = resourceConfig.properties();

		serverPort = getIntProperty("app.port");
		appName = props.getProperty("app.name");
		idleTimeout = getIntProperty("app.idle-timeout");
		logFile = props.getProperty("app.log-file");
		acceptQueueSize = getIntProperty("app.accept-queue-size");
		logRetainDays = getIntProperty("app.log-file-retain-days");
		startDispatcher = getBooleanProperty("app.start-dispatcher");
		watchdogSleepTime = getIntProperty("app.watchdog-sleep-time");

		workResultsIngesterUrl = props.getProperty("app.work-results-ingester");

		if (!StringUtils.isBlank(logFile))
			try {
				// We are configuring a RolloverFileOutputStream with file name pattern and
				// appending property
				RolloverFileOutputStream os;
				os = new RolloverFileOutputStream(logFile, true, logRetainDays);

				// We are creating a print stream based on our RolloverFileOutputStream
				PrintStream logStream = new PrintStream(os);

				// We are redirecting system out and system error to our print stream.
				System.setOut(logStream);
				System.setErr(logStream);

			} catch (IOException e) {
				e.printStackTrace();
			}

		// SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
		server = new Server();

		ServerConnector connector = new ServerConnector(server);
		connector.setIdleTimeout(idleTimeout);
		connector.setAcceptQueueSize(acceptQueueSize);
		connector.setPort(serverPort);

		server.addConnector(connector);

		// java.net.URL classUrl =
		// this.getClass().getResource("com.sun.mail.util.TraceInputStream");
		// System.out.println(classUrl.getFile());

		HandlerCollection handlers = new HandlerCollection();

		// here we will hold start page for our web service, should it have to serve
		// some public pages (index.html with some basic info for beggining)
		WebAppContext webApp = new WebAppContext();

		URL webRootLocation = RobnoRestApp.class.getResource("/webapp/");
		URI webRootUri;
		try {
			webRootUri = URI.create(webRootLocation.toURI().toASCIIString().replaceFirst("/index.html$", "/"));

			webApp.setContextPath("/html");
			webApp.setBaseResource(org.eclipse.jetty.util.resource.Resource.newResource(webRootUri));
			webApp.setWelcomeFiles(new String[] { "index.html" });

			handlers.addHandler(webApp);
		} catch (URISyntaxException | MalformedURLException e) {
			e.printStackTrace();
		}

		// rest services will live under '/' and all classes serving them will
		// be in 'resource' package holding 'v1' prefix with them so that we can build
		// 'v2' later
		ServletContextHandler restHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		restHandler.setContextPath("/" + appName);

		restHandler.addServlet(new ServletHolder(new ServletContainer(new RobnoRestResourceConfig())), "/*");

		restHandler.addFilter(AuthFilter.class, "/v1/*", EnumSet.of(DispatcherType.REQUEST));

		handlers.addHandler(restHandler);
		server.setHandler(handlers);

	}

	public void start() throws Exception {
		server.start();
		server.dump(System.err);
		System.out.println("Server started...");

		LOG.info("RobnoRest server started, listening on port " + serverPort + " App context: /" + appName
				+ " Log file: " + logFile);

		server.join();
	}

	public static void main(String args[]) throws Exception {
		RobnoRestApp theServer = new RobnoRestApp();
		theServer.setup();
		theServer.start();
	}

	public final static Integer getLoggedUserIdForUUID(UUID uuid) {
		if (uuid == null)
			return null;
		return loggedUsers.get(uuid);
	}

	public final static UUID logUser(Integer userId) {
		if (userId < 1)
			return null;

		UUID uuid = UUID.randomUUID();
		loggedUsers.put(uuid, userId);
		return uuid;
	}

	private int getIntProperty(String key) {
		return Integer.parseInt(props.getProperty(key));
	}

	private boolean getBooleanProperty(String key) {
		return Boolean.parseBoolean(props.getProperty(key));
	}

}