package net.mdp3.java.util.webservice;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class WebserviceBase {
	private final static String CLAZZ = WebserviceBase.class.getName();
	private final static Logger LOG = Logger.getLogger(CLAZZ);

	//private boolean running = true;
	protected int port;
	protected String name = "ws";
	
	protected HttpServer server;
	
	protected HttpHandler handler;
	
	public WebserviceBase(int port, String name, HttpHandler listener) throws BindException, IOException {
		LOG.entering(CLAZZ, "Webservice", "port:" + port + " name:" + name);
		if (name.charAt(0) != '/') name = "/" + name; //HttpServer.createContext arg0 is required to begin with /
		
		this.port = port;
		this.name = name;
		this.handler = listener;
		
		startServer();
		
		LOG.exiting(CLAZZ, "Webservice");
	}
	
	public void startServer() throws BindException, IOException {
		LOG.entering(CLAZZ, "startServer");
		
		server = HttpServer.create(new InetSocketAddress(this.port), 0);
		server.createContext(this.name, handler);
		server.setExecutor(null);
		server.start();
		
		LOG.exiting(CLAZZ, "startServer");
	}
	
	/**
	 * Stops the webservice
	 * 
	 * @param delay
	 * @throws Exception
	 */
	public void stopServer(int delay) throws Exception {
		LOG.entering(CLAZZ, "stopServer");
		
		server.stop(delay);
		
		LOG.exiting(CLAZZ, "stopServer");
	}
	
	/**
	 * Restarts the service and can change the port
	 * 
	 * @param delay
	 * @param newPort
	 * @throws Exception
	 */
	public void restart(int delay, int newPort) throws Exception {
		LOG.entering(CLAZZ, "restart");
		
		stopServer(delay);
		this.port = newPort;
		startServer();
		
		LOG.exiting(CLAZZ, "restart");
	}
	
	public HttpHandler getHandler() {
		return handler;
	}

	public void setHandler(HttpHandler handler) {
		this.handler = handler;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
